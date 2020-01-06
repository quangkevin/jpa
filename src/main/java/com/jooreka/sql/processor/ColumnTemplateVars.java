package com.jooreka.sql.processor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.persistence.Column;
import javax.persistence.Id;

public class ColumnTemplateVars {
  public static ColumnTemplateVars create(Column column, ExecutableElement element) {
    ColumnTemplateVars result = new ColumnTemplateVars();

    result.columnName = "".equals(column.name()) ? null : column.name();
    result.unique = column.unique();
    result.nullable = column.nullable();
    result.insertable = column.insertable();
    result.updatable = column.updatable();
    result.columnDefinition = column.columnDefinition();
    result.length = column.length();
    result.isId = element.getAnnotation(Id.class) != null;

    if (element.getParameters().isEmpty()) {
      result.getter = element;
      result.javaType = element.getReturnType();
    }

    if (element.getParameters().size() == 1) {
      result.setter = element;
      element.getParameters().get(0).asType();
    }

    if (result.columnName == null) {
      if (result.getter != null) {
        result.columnName = Converter.camelCaseToSnakeCase(result.getGetterMethodName().startsWith("get")
							   ? result.getGetterMethodName().substring(3)
							   : result.getGetterMethodName());
      } else if (result.setter != null) {
        result.columnName = Converter.camelCaseToSnakeCase(result.getSetterMethodName().startsWith("set")
							   ? result.getSetterMethodName().substring(3)
							   : result.getSetterMethodName());
      }
    }

    result.columnName = result.columnName.toLowerCase();
    result.fieldName = Converter.snakeCaseToCamelCase(result.columnName);

    return result;
  }

  private String fieldName;
  private String columnName;
  private Boolean unique;
  private Boolean nullable;
  private Boolean insertable;
  private Boolean updatable;

  private String columnDefinition;
  private Integer length;

  private boolean isId;

  private TypeMirror javaType;
  private ExecutableElement getter;
  private ExecutableElement setter;

  private ColumnTemplateVars() {}

  public void merge(ColumnTemplateVars column) {
    if (unique == null) unique = column.unique;
    if (nullable == null) nullable = column.nullable;
    if (insertable == null) insertable = column.insertable;
    if (updatable == null) updatable = column.updatable;
    if (columnDefinition == null) columnDefinition = column.columnDefinition;
    if (length == null) length = column.length;
    if (!isId) isId = column.isId;
    if (setter == null) setter = column.setter;
    if (getter == null) getter = column.getter;    
  }

  public String getFieldName() {
    return fieldName;
  }

  public String getColumnName() {
    return columnName;
  }
  
  public String getSqlRowNextMethodName() {
    return "next" + getSqlType();
  }

  public String getSqlRowSetMethodName() {
    return "set" + getSqlType();
  }

  public boolean getUnique() {
    return Boolean.TRUE.equals(unique);
  }

  public boolean getNullable() {
    return Boolean.TRUE.equals(nullable);
  }

  public boolean getInsertable() {
    return Boolean.TRUE.equals(insertable);
  }

  public boolean getUpdatable() {
    return Boolean.TRUE.equals(updatable);
  }

  public String getColumnDefinition() {
    return columnDefinition;
  }

  public Integer getLength() {
    return length;
  }

  public boolean isId() {
    return isId;
  }

  public TypeMirror getJavaType() {
    return javaType;
  }

  public String getGetterMethodName() {
    return getter == null ? null : getter.getSimpleName().toString();
  }

  public String getSetterMethodName() {
    return setter == null ? null : setter.getSimpleName().toString();
  }

  public String getSetterReturnType() {
    return setter == null ? null : setter.getReturnType().toString();
  }

  private String getSqlType() {
    String type = null;
    
    switch (javaType.getKind()) {
    case BOOLEAN:
    case BYTE: 
    case FLOAT:
    case DOUBLE:
    case INT: 
    case LONG:
      type = javaType.getKind().name().toLowerCase();
      type = type.substring(0, 1).toUpperCase() + type.substring(1);
      break;

    case DECLARED:
      type = ((DeclaredType) javaType).asElement().getSimpleName().toString();
      break;

    case ARRAY:
      type = "Bytes";
      break;
    }

    return type;
  }
}
