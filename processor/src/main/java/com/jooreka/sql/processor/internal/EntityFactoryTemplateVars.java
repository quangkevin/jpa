package com.jooreka.sql.processor.internal;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.jooreka.sql.processor.EntityFactory;
import com.jooreka.sql.processor.Table;

public class EntityFactoryTemplateVars {
  public static EntityFactoryTemplateVars create(EntityFactory factory, TypeElement element) {
    EntityFactoryTemplateVars result = new EntityFactoryTemplateVars();

    PackageElement packageElement = Converter.getPackageElement(element);
    result.type = element;
    result.packageName = packageElement.isUnnamed() ? null : packageElement.toString();
    result.implClassName = (element
			    .getQualifiedName()
			    .toString()
			    .substring(result.packageName == null
				       ? 0
				       : (result.packageName.length() + 1))
			    .replace(".", "$")
			    + "$Impl");
    
    return result;
  }

  private static void validate(TypeElement element) {
    if (element.getKind() != ElementKind.CLASS && element.getKind() != ElementKind.INTERFACE) {
      throw new InvalidEntityException(element, "Entity factory is not a class or interface");
    }

    if (element.getModifiers().contains(Modifier.PRIVATE)) {
      throw new InvalidEntityException(element, "Entity factory cannot be private");
    }

    if (element.getKind() == ElementKind.CLASS) {
      if (element.getModifiers().contains(Modifier.FINAL)) {
	throw new InvalidEntityException(element, "Entity factory class cannot be final");
      }

      if (!(element.getEnclosingElement() instanceof PackageElement)
	  && !element.getModifiers().contains(Modifier.STATIC)) {
	throw new InvalidEntityException(element, "Entity factory inner class must be static");
      }
    }    
  }

  private String packageName;
  private String implClassName;
  private List<EntityTemplateVars> entityVars = new ArrayList<>();
  private TypeElement type;

  private EntityFactoryTemplateVars() {}

  public TypeElement getType() { return type; }
  public String getPackageName() { return packageName; }
  public String getImplClassName() { return implClassName; }
  public boolean getIsInterface() { return type.getKind() == ElementKind.INTERFACE; }

  public String getImplQualifiedClassName() {
    return (packageName == null
	    ? implClassName
	    : (packageName + "." + implClassName));
  }

  public String getQualifiedClassName() {
    return type.getQualifiedName().toString();
  }
  
  public List<EntityTemplateVars> getEntityTemplateVars() { return entityVars; }
  public void addEntityTemplateVars(EntityTemplateVars vars) { entityVars.add(vars); }
}
