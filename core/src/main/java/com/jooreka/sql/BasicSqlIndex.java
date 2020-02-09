package com.jooreka.sql;

import java.util.List;

public class BasicSqlIndex implements SqlIndex {
  private final String name;
  private final List<String> columnNames;
  private final boolean isUnique;
    
  public BasicSqlIndex(String name, List<String> columnNames, boolean isUnique) {
    this.name = name;
    this.columnNames = columnNames;
    this.isUnique = isUnique;
  }

  public String getName() { return name; }
  public List<String> getColumnNames() { return columnNames; }
  public boolean isUnique() { return isUnique; }
}
