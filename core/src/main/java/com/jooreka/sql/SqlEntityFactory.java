package com.jooreka.sql;

import java.util.function.Function;

public interface SqlEntityFactory {
  public <T extends SqlEntity> SqlTable<T> getTable(Class<T> entityClass);
  public String getSchemaDefinition(Function<SqlTable<?>, String> tableToDefinition);
}
