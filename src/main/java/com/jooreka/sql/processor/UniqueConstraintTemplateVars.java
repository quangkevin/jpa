package com.jooreka.sql.processor;

import javax.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class UniqueConstraintTemplateVars {
  public static UniqueConstraintTemplateVars create(UniqueConstraint val) {
    UniqueConstraintTemplateVars result = new UniqueConstraintTemplateVars();
    result.name = val.name();
    result.columnNames = Arrays.asList(val.columnNames());

    return result;
  }
  
  private String name;
  private List<String> columnNames = new ArrayList<>();

  public String getName() { return name; }
  public List<String> getColumnNames() { return columnNames; }

  @Override public int hashCode() { return name.hashCode(); }
  
  @Override public boolean equals(Object o) {
    return (o instanceof UniqueConstraintTemplateVars
	    && name.equals(((UniqueConstraintTemplateVars) o).name));
  }
}
