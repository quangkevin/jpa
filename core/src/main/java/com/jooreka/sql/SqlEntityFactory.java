package com.jooreka.sql;

public interface SqlEntityFactory {
  public <T extends SqlEntity> SqlTable<T> getTable(Class<T> entityClass);
}
