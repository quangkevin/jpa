package com.jooreka.sql.processor;

import javax.persistence.Index;
import javax.persistence.UniqueConstraint;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IndexTemplateVars {
  public static IndexTemplateVars create(Index index) {
    return new IndexTemplateVars(index.name(),
				 Arrays.asList(index.columnList().split(",")),
				 index.unique());
  }

  public static IndexTemplateVars create(UniqueConstraint unique) {
    return new IndexTemplateVars(unique.name(), 
				 Arrays.asList(unique.columnNames()),
				 true);
  }
  
  private String name;
  private List<String> columns;
  private boolean unique;  

  private IndexTemplateVars(String name, List<String> columns, boolean unique) {
    if (name == null || name.length() == 0) {
      name = columns.stream().collect(Collectors.joining("_"));
    }

    this.name = name;
    this.columns = columns;
    this.unique = unique;
  }

  public String getName() {
    return name;
  }

  public List<String> getColumns() {
    return columns;
  }

  public boolean getIsUnique() {
    return unique;
  }

  @Override public boolean equals(Object o) {
    return (o instanceof IndexTemplateVars
	    && name.equals(((IndexTemplateVars) o).name));
  }

  @Override public int hashCode() {
    return name.hashCode();
  }
}
