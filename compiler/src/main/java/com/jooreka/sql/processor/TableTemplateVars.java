package com.jooreka.sql.processor;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.*;
import java.util.stream.Collectors;

public class TableTemplateVars {
  public static TableTemplateVars create(Entity entity, TypeElement element) {
    validate(element);

    TableTemplateVars result = new TableTemplateVars();
    result.tableName = "".equals(entity.name()) ? null : entity.name();
    result.type = element;

    if (result.tableName == null) {
      result.tableName = Converter.camelCaseToSnakeCase(element.getSimpleName().toString()).toLowerCase();
    }    

    return result;
  }

  public static TableTemplateVars create(Table table, TypeElement element) {
    validate(element);
    
    TableTemplateVars result = new TableTemplateVars();

    result.type = element;
    result.tableName = "".equals(table.name()) ? null : table.name();
    result.database = "".equals(table.catalog()) ? null : table.catalog();

    if (result.database == null && !"".equals(table.schema())) {
      result.database = table.schema();
    }

    if (table.indexes() != null) {
      result.indices.addAll(Arrays
			    .stream(table.indexes())
			    .map(IndexTemplateVars::create)
			    .collect(Collectors.toList()));
    }

    if (table.uniqueConstraints() != null) {
      result.indices.addAll(Arrays
			    .stream(table.uniqueConstraints())
			    .map(IndexTemplateVars::create)
			    .collect(Collectors.toList()));
    }

    if (result.tableName == null) {
      result.tableName = Converter.camelCaseToSnakeCase(element.getSimpleName().toString()).toLowerCase();
    }

    return result;
  }

  private static void validate(TypeElement element) {
    if (element.getKind() != ElementKind.CLASS && element.getKind() != ElementKind.INTERFACE) {
      throw new InvalidEntityException(element, "Table entity is not a class or interface");
    }

    if (element.getModifiers().contains(Modifier.PRIVATE)) {
      throw new InvalidEntityException(element, "Table entity cannot be private");
    }

    if (element.getKind() == ElementKind.CLASS) {
      if (element.getModifiers().contains(Modifier.FINAL)) {
	throw new InvalidEntityException(element, "Table entity class cannot be final");
      }

      if (!(element.getEnclosingElement() instanceof PackageElement)
	  && !element.getModifiers().contains(Modifier.STATIC)) {
	throw new InvalidEntityException(element, "Table entity inner class must be static");
      }
    }
  }

  private String database;
  private String tableName;
  private Set<ColumnTemplateVars> columns = new LinkedHashSet<>();
  private Set<IndexTemplateVars> indices = new LinkedHashSet<>();

  private TypeElement type;

  private TableTemplateVars() {}

  public void merge(TableTemplateVars proto) {
    if (database == null) this.database = proto.database;

    this.columns.addAll(proto.columns);
    this.indices.addAll(proto.indices);
  }

  public boolean getIsInterface() { return type.getKind() == ElementKind.INTERFACE; }

  public TypeElement getType() { return type; }
  
  public String getPackage() {
    Element pkg = type.getEnclosingElement();

    while (!(pkg instanceof PackageElement)) {
      pkg = pkg.getEnclosingElement();
    }
    
    return ((type.getEnclosingElement() instanceof PackageElement
	     && !((PackageElement) type.getEnclosingElement()).isUnnamed())
	    ? type.getEnclosingElement().toString()
	    : null);
  }
  
  public String getQualifiedEntityName() { return type.getQualifiedName().toString(); }

  public String getImplEntitySimpleName() {
    String pkg = getPackage();

    String simpleName = (pkg == null
			 ? getQualifiedEntityName()
			 : getQualifiedEntityName().substring(pkg.length() + 1));

    return simpleName.replace(".", "$") + "$Impl";
  }
  
  public String getImplEntityQualifiedName() {
    String pkg = getPackage();
    
    return (null == pkg ? "" : (pkg + ".")) + getImplEntitySimpleName();
  }

  public int getIdColumnIndex() {
    int index = -1;
    for (ColumnTemplateVars column : columns) {
      ++index;
      if (column.getIsId()) return index;
    }

    return -1;
  }
  
  public ColumnTemplateVars getIdColumn() {
    return columns.stream().filter(x -> x.getIsId()).findFirst().orElse(null);
  }

  public String getInsertStatement() {
    Collection<ColumnTemplateVars> columns = getColumnsForInsert();
    
    return String.format("INSERT INTO %s(%s) VALUES (%s)",
			 getTableName(),
			 columns.stream().map(x -> x.getColumnName()).collect(Collectors.joining(", ")),
			 columns.stream().map(x -> "?").collect(Collectors.joining(", ")));
  }

  public String getUpdateStatement() {
    Collection<ColumnTemplateVars> columns = getColumnsForUpdate();
    
    return String.format("UPDATE %s SET %s WHERE %s = ?",
			 getTableName(),
			 columns.stream()
			 .filter(x -> !x.getIsId())
			 .map(x -> x.getColumnName() + " = ?")
			 .collect(Collectors.joining(", ")),
			 getIdColumn().getColumnName());
  }

  public String getDeleteStatement() {
    return String.format("DELETE FROM %s WHERE %s = ?",
			 getTableName(),
			 getIdColumn().getColumnName());
  }

  public String getDatabase() {
    return database;
  }

  public String getTableName() {
    return tableName;
  }

  public Set<ColumnTemplateVars> getColumns() {
    return columns;
  }

  public List<ColumnTemplateVars> getColumnsForInsert() {
    return getColumns().stream().filter(x -> x.getInsertable()).collect(Collectors.toList());
  }

  public List<ColumnTemplateVars> getColumnsForUpdate() {
    return getColumns().stream().filter(x -> x.getUpdatable()).collect(Collectors.toList());
  }

  public Set<IndexTemplateVars> getIndices() {
    return indices;
  }

  public boolean hasFlushMethod() {
    return hasMethod("flush");
  }

  public boolean hasDeleteMethod() {
    return hasMethod("delete");
  }  

  private boolean hasMethod(String name) {
    for (Element e : getType().getEnclosedElements()) {
      if (ElementKind.METHOD == e.getKind()
	  && name.equals(((ExecutableElement) e).getSimpleName().toString())
	  && ((ExecutableElement) e).getParameters().isEmpty()) {
	return true;
      }
    }

    return false;    
  }
}
