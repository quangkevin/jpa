package com.jooreka.sql.processor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.persistence.Column;
import javax.persistence.Id;

public class ColumnTemplateVars {
  public static ColumnTemplateVars create(Column column, ExecutableElement element) {
    validate(element);
    
    ColumnTemplateVars result = new ColumnTemplateVars();

    result.columnName = "".equals(column.name()) ? null : column.name();
    result.unique = column.unique();
    result.isId = element.getAnnotation(Id.class) != null;    
    result.nullable = !result.isId && column.nullable();
    result.insertable = column.insertable();
    result.updatable = column.updatable();
    result.columnDefinition = "".equals(column.columnDefinition()) ? null : column.columnDefinition();
    result.length = column.length();

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

  private static void validate(ExecutableElement element) {
    if (element.getModifiers().contains(Modifier.PRIVATE)) {
      throw new InvalidEntityException(element, "Column method must not be private");
    }

    if (!element.getModifiers().contains(Modifier.ABSTRACT)) {
      throw new InvalidEntityException(element, "Column method must be abstract");
    }

    if (element.getModifiers().contains(Modifier.STATIC)) {
      throw new InvalidEntityException(element, "Column method must not be static");
    }
    
    if (1 < element.getParameters().size()) {
      throw new InvalidEntityException(element, "Looks like a setter but it has more than 1 parameter");
    }
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
    if (nullable == null || nullable) nullable = column.nullable;
    if (insertable == null || insertable) insertable = column.insertable;
    if (updatable == null || updatable) updatable = column.updatable;
    if (columnDefinition == null) columnDefinition = column.columnDefinition;
    if (length == null) length = column.length;
    if (!isId) isId = column.isId;
    if (setter == null) setter = column.setter;
    if (getter == null) getter = column.getter;

    if (getter != null
	&& setter != null
	&& !getter.getReturnType().equals(setter.getParameters().get(0).asType())) {
      throw new InvalidEntityException(setter, "Getter %s is not compatibile with setter %s [%s != %s]",
				       getter,
				       setter,
				       getter.getReturnType(),
				       setter.getParameters().get(0).asType());
    }
  }

  public String getFieldName() {
    return fieldName;
  }

  public String getColumnName() {
    return columnName;
  }
  
  public String getSqlRowNextMethodName() {
    return "next" + getSqlTypeName();
  }

  public String getSqlRowSetMethodName() {
    String typeName = getSqlTypeName();
    
    return "set" + ("Integer".equals(typeName) ? "Int" : typeName);
  }

  public String getSqlType() {
    String type = javaType.getKind().name();
    
    switch (javaType.getKind()) {
    case DECLARED:
      type = ((DeclaredType) javaType).asElement().getSimpleName().toString().toUpperCase();
      break;

    default:
      type = javaType.getKind().name();
    }

    switch (type) {
    case "BOOLEAN": return "Types.BOOLEAN";
    case "BYTE": return "Types.TINYINT";
    case "FLOAT": return "Types.FLOAT";
    case "DOUBLE": return "Types.DOUBLE";
    case "INTEGER": return "Types.INTEGER";      
    case "INT": return "Types.INTEGER";
    case "LONG": return "Types.BIGINT";
    case "ARRAY": return "Types.BLOB";
    case "STRING": return "Types.VARCHAR";
    default: return "Types.BLOB";
    }
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
    String result = columnDefinition;
    
    if (null == result) {
      result = getSqlType().substring("Types.".length());

      result = (getLength() == null
		? result
		: String.format("%s(%d)", result, getLength()));

      if (getIsId()) {
	result += " PRIMARY KEY";
      }

      if (!getNullable()) {
	result += " NOT NULL";
      }
    }

    return result;
  }

  public Integer getLength() {
    return ("Types.VARCHAR".equals(getSqlType())
	    ? length
	    : null);
  }

  public boolean getIsId() {
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

  private String getSqlTypeName() {
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
