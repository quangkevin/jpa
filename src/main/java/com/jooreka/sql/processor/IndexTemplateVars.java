package com.jooreka.sql.processor;

import javax.persistence.Index;
import java.util.Arrays;
import java.util.List;

class IndexTemplateVars {
  public static IndexTemplateVars create(Index index) {
    IndexTemplateVars result = new IndexTemplateVars();
    result.name = index.name();
    result.columns = Arrays.asList(index.columnList().split(","));

    return result;
  }
  
  private String name;
  private List<String> columns;

  private IndexTemplateVars() {}

  public String getName() {
    return name;
  }

  public List<String> getColumns() {
    return columns;
  }

  public boolean isUnique() {
    return unique;
  }

  private boolean unique;

  @Override public boolean equals(Object o) {
    return (o instanceof IndexTemplateVars
	    && name.equals(((IndexTemplateVars) o).name));
  }

  @Override public int hashCode() {
    return name.hashCode();
  }
}
