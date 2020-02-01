package com.jooreka.sql;

import java.util.List;

public interface SqlIndex {
  public String getName();
  public List<String> getColumnNames();
  public boolean isUnique();
}
