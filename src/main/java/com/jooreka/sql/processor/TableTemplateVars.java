package com.jooreka.sql.processor;

import javax.lang.model.element.TypeElement;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.*;
import java.util.stream.Collectors;

public class TableTemplateVars {
  public static TableTemplateVars create(Entity entity, TypeElement element) {
    TableTemplateVars result = new TableTemplateVars();
    result.tableName = "".equals(entity.name()) ? null : entity.name();
    result.type = element;

    if (result.tableName == null) {
      result.tableName = Converter.camelCaseToSnakeCase(element.getSimpleName().toString()).toLowerCase();
    }    

    return result;
  }

  public static TableTemplateVars create(Table table, TypeElement element) {
    TableTemplateVars result = new TableTemplateVars();

    result.type = element;
    result.tableName = "".equals(table.name()) ? null : table.name();
    result.database = "".equals(table.catalog()) ? null : table.catalog();

    if (result.database == null && !"".equals(table.schema())) {
      result.database = table.schema();
    }

    if (table.indexes() != null) {
      result.indices.addAll(Arrays.stream(table.indexes()).map(IndexTemplateVars::create).collect(Collectors.toList()));
    }

    if (table.uniqueConstraints() != null) {
      result.uniqueConstraints.addAll(Arrays.stream(table.uniqueConstraints()).map(UniqueConstraintTemplateVars::create).collect(Collectors.toList()));
    }

    if (result.tableName == null) {
      result.tableName = Converter.camelCaseToSnakeCase(element.getSimpleName().toString()).toLowerCase();
    }

    return result;
  }

  private String database;
  private String tableName;
  private Set<ColumnTemplateVars> columns = new LinkedHashSet<>();
  private Set<UniqueConstraintTemplateVars> uniqueConstraints = new LinkedHashSet<>();
  private Set<IndexTemplateVars> indices = new LinkedHashSet<>();

  private TypeElement type;

  private TableTemplateVars() {}

  public void merge(TableTemplateVars proto) {
    if (database == null) this.database = proto.database;

    this.columns.addAll(proto.columns);
    this.uniqueConstraints.addAll(proto.uniqueConstraints);
    this.indices.addAll(proto.indices);
  }
  
  public TypeElement getType() { return type; }
  public String getQualifiedEntityName() { return getType().getQualifiedName().toString(); }
  public String getEntitySimpleName() { return getType().getSimpleName().toString(); }

  public String getImplEntitySimpleName() {
    return getEntitySimpleName() + "_Imp";
  }
  
  public String getImplEntityQualifiedName() {
    return getType().getEnclosingElement() + "." + getImplEntitySimpleName();
  }
  
  public ColumnTemplateVars getIdColumn() {
    return columns.stream().filter(x -> x.isId()).findFirst().orElse(null);
  }

  public String getInsertStatement() {
    return String.format("INSERT INTO %s(%s) VALUES (%s)",
			 getTableName(),
			 columns.stream().map(x -> x.getColumnName()).collect(Collectors.joining(", ")),
			 columns.stream().map(x -> "?").collect(Collectors.joining(", ")));
  }

  public String getUpdateStatement() {
    return String.format("UPDATE %s SET %s WHERE %s = ?",
			 getTableName(),
			 columns.stream()
			 .filter(x -> !x.isId())
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

  public Set<UniqueConstraintTemplateVars> getUniqueConstraints() {
    return uniqueConstraints;
  }

  public Set<IndexTemplateVars> getIndices() {
    return indices;
  }
}
