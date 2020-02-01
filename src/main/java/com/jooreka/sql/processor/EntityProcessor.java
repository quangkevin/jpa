package com.jooreka.sql.processor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.annotation.processing.*;
import javax.lang.model.*;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.*;
import javax.persistence.Column;
import javax.persistence.Table;
import javax.tools.*;

import com.google.escapevelocity.Template;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;

@SupportedAnnotationTypes({"javax.persistence.Table", "javax.persistence.Entity"})
public class EntityProcessor extends AbstractProcessor {
  private Filer filer;
  private ErrorReporter errorReporter;

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
    Template entityTemplate = parseTemplate("entity.vm");

    for (TableTemplateVars tableVars : createTemplateVars(round)) {
      Map<String, Object> vars = new HashMap<>();
      vars.put("table", tableVars);

      writeSourceFile(tableVars.getImplEntityQualifiedName(),
		      entityTemplate.evaluate(vars),
		      tableVars.getType());
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
      errorReporter.warn("Could not write generated class " + className + ": " + e, originatingType);
    }
  }

  private Collection<TableTemplateVars> createTemplateVars(RoundEnvironment round) {
    Map<String, TableTemplateVars> vars = new HashMap<>();

    for (Element element : round.getElementsAnnotatedWith(Table.class)) {
      TypeElement classElement = (TypeElement) element;
      Table table = classElement.getAnnotation(Table.class);
      TableTemplateVars tableTemplateVars = TableTemplateVars.create(table, classElement);
      TableTemplateVars existingTableTemplateVars = vars.get(tableTemplateVars.getQualifiedEntityName());

      if (existingTableTemplateVars == null) {
        vars.put(tableTemplateVars.getQualifiedEntityName(), tableTemplateVars);

      } else {
        existingTableTemplateVars.merge(tableTemplateVars);
        tableTemplateVars = existingTableTemplateVars;
      }

      for (Element enclosing : element.getEnclosedElements()) {
        Column column = enclosing.getAnnotation(Column.class);

        if (column != null && enclosing.getKind() == ElementKind.METHOD) {
          ExecutableElement methodElement = (ExecutableElement) enclosing;

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
        }
      }
    }

    return vars.values();
  }

  private void debug(TableTemplateVars vars) {
    System.out.println(new com.google.gson.GsonBuilder()
		       .setPrettyPrinting()
		       .registerTypeAdapter(TypeMirror.class, new com.google.gson.JsonSerializer<TypeMirror>() {
			 public com.google.gson.JsonElement serialize(TypeMirror val, java.lang.reflect.Type typeOfSrc, com.google.gson.JsonSerializationContext context) {
			   return new com.google.gson.JsonPrimitive(String.valueOf(val));
			 }
		       })
		       .registerTypeAdapter(TypeElement.class, new com.google.gson.JsonSerializer<TypeElement>() {
			 public com.google.gson.JsonElement serialize(TypeElement val, java.lang.reflect.Type typeOfSrc, com.google.gson.JsonSerializationContext context) {
			   return new com.google.gson.JsonPrimitive(val.getQualifiedName().toString());
			 }
		       })
		       .create()
		       .toJson(vars));
  }

  private void printf(String message, Object... args) {
    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(message, args));
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

  private static class Foo {
    int x = 2;
    Set<String> y = new LinkedHashSet<>();
  }
}
