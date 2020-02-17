package com.jooreka.sql.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

import com.google.escapevelocity.Template;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;

import com.jooreka.sql.processor.internal.ColumnTemplateVars;
import com.jooreka.sql.processor.internal.Converter;
import com.jooreka.sql.processor.internal.EntityFactoryTemplateVars;
import com.jooreka.sql.processor.internal.ErrorReporter;
import com.jooreka.sql.processor.internal.IndexTemplateVars;
import com.jooreka.sql.processor.internal.InvalidEntityException;
import com.jooreka.sql.processor.internal.EntityTemplateVars;

@SupportedAnnotationTypes({"com.jooreka.sql.processor.EntityFactory", "com.jooreka.sql.processor.Table", "com.jooreka.sql.processor.Entity"})
public class EntityProcessor extends AbstractProcessor {
  private Filer filer;
  private ErrorReporter errorReporter;
  private Column defaultColumn = AnnotationDefaults.of(Column.class);

  private boolean done;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);

    this.filer = processingEnv.getFiler();
    this.errorReporter = new ErrorReporter(processingEnv.getMessager());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations,
                         RoundEnvironment round)
  {
    Template entityFactoryTemplate = parseTemplate("entity_factory.vm");
    Template entityTemplate = parseTemplate("entity.vm");

    EntityFactoryTemplateVars factoryVars = createEntityFactoryTemplateVars(round);

    for (EntityTemplateVars entityTemplateVars : createEntityTemplateVars(round)) {
      Map<String, Object> vars = new HashMap<>();
      vars.put("vars", entityTemplateVars);

      writeSourceFile(entityTemplateVars.getQualifiedOuterClassName(),
		      entityTemplate.evaluate(vars),
		      entityTemplateVars.getType());

      if (entityTemplateVars != null && factoryVars != null) {
	factoryVars.addEntityTemplateVars(entityTemplateVars);
      }
    }

    if (factoryVars != null && !factoryVars.getEntityTemplateVars().isEmpty()) {
      Map<String, Object> vars = new HashMap<>();
      vars.put("vars", factoryVars);
      writeSourceFile(factoryVars.getImplQualifiedClassName(),
		      entityFactoryTemplate.evaluate(vars),
		      factoryVars.getType());
    }

    return true;
  }

  private void writeSourceFile(String className, String text, TypeElement originatingType) {
    try {
      JavaFileObject sourceFile = filer.createSourceFile(className, originatingType);

      try (Writer writer = sourceFile.openWriter()) {
        writer.write(new Formatter().formatSource(text));
      }
    } catch (IOException | FormatterException e) {
      errorReporter.warn(originatingType, "Could not write generated class " + className + ": " + e);
    }
  }

  private EntityFactoryTemplateVars createEntityFactoryTemplateVars(RoundEnvironment round) {
    EntityFactoryTemplateVars result = null;
    
    for (Element element : round.getElementsAnnotatedWith(EntityFactory.class)) {
      if (result != null) {
	errorReporter.err(element, "Duplicated usage of @EntityFactory. Already used by " + result.getType());
	
      } else {
	try {
	  result = EntityFactoryTemplateVars.create(element.getAnnotation(EntityFactory.class), (TypeElement) element);
	} catch (InvalidEntityException e) {
	  errorReporter.err(e.getElement(), e.getMessage());
	}
      }
    }

    return result;
  }

  private Collection<EntityTemplateVars> createEntityTemplateVars(RoundEnvironment round) {
    Map<String, EntityTemplateVars> vars = new HashMap<>();

    for (Element element : round.getElementsAnnotatedWith(Table.class)) {
      try {
        EntityTemplateVars entity = processEntity(element);
        vars.put(entity.getQualifiedOuterClassName(), entity);

      } catch (InvalidEntityException exception) {
        errorReporter.err(exception.getElement(), exception.getMessage());
      }
    }

    return vars.values();
  }

  private EntityTemplateVars processEntity(Element element) {
    TypeElement classElement = (TypeElement) element;
    Table table = classElement.getAnnotation(Table.class);
    EntityTemplateVars tableTemplateVars = EntityTemplateVars.create(table, classElement);

    for (Element enclosing : element.getEnclosedElements()) {
      Column column = enclosing.getAnnotation(Column.class);

      if (column != null && enclosing.getKind() != ElementKind.METHOD) {
        errorReporter.err(enclosing, "@Column can only be used on abstract method");

      } else if (column == null
          && enclosing.getKind() == ElementKind.METHOD
          && enclosing.getModifiers().contains(Modifier.ABSTRACT)) {
        column = defaultColumn;
      }

      if (column != null) {
        ExecutableElement methodElement = (ExecutableElement) enclosing;

        try {
          ColumnTemplateVars columnTemplateVars = ColumnTemplateVars.create(column, methodElement);
          Optional<ColumnTemplateVars> existingColumnProto = (tableTemplateVars
              .getColumns()
              .stream()
              .filter(x -> x.getColumnName().equals(columnTemplateVars.getColumnName()))
              .findAny());
          if (existingColumnProto.isPresent()) {
            existingColumnProto.get().merge(columnTemplateVars);

          } else {
            tableTemplateVars.getColumns().add(columnTemplateVars);
          }
        } catch (InvalidEntityException e) {
          errorReporter.err(enclosing, e.getMessage());
        }
      }
    }

    if (tableTemplateVars.getColumns().isEmpty()) {
      throw new InvalidEntityException(element, "Missing columns");
    }

    return tableTemplateVars;
  }

  private Template parseTemplate(String resourceName) {
    try {
      return Template.parseFrom(readerFromResource(resourceName));

    } catch (IOException e) {
      throw new AssertionError(e);
    }
  }

  private Reader readerFromResource(String resourceName) {
    InputStream in = EntityProcessor.class.getResourceAsStream("/templates/" + resourceName);

    if (in == null) {
      throw new IllegalArgumentException("Could not find resource: " + resourceName);
    }

    return new InputStreamReader(in, StandardCharsets.UTF_8);
  }

  private static class AnnotationDefaults implements InvocationHandler {
    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A of(Class<A> annotation) {
      return (A) Proxy.newProxyInstance(annotation.getClassLoader(),
					new Class[] {annotation}, new AnnotationDefaults());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
      return method.getDefaultValue();
    }
  }
}
