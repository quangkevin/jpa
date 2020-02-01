package com.jooreka.sql;

public interface SqlColumn {
  public String getName();
  public int getType();
  public Integer getLength();
  public String getDefinition();  
  public boolean isUnique();
  public boolean isNullable();
  public boolean isInsertable();
  public boolean isUpdatable();
  public boolean isId();  
}
