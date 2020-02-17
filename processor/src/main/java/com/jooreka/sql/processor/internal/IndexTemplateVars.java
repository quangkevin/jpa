package com.jooreka.sql.processor.internal;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.jooreka.sql.processor.Index;
import com.jooreka.sql.processor.UniqueConstraint;

public class IndexTemplateVars {
  public static IndexTemplateVars create(Index index) {
    return new IndexTemplateVars(index.name(),
				 Arrays.asList(index.columns()),
				 index.unique());
  }

  public static IndexTemplateVars create(UniqueConstraint unique) {
    return new IndexTemplateVars(unique.name(), 
				 Arrays.asList(unique.columns()),
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
