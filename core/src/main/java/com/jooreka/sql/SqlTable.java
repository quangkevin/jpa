package com.jooreka.sql;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface SqlTable<T extends SqlEntity> {
  public SqlConnectionSession getSession();
  public String getTableName();
  public String getDatabaseName();
  public List<SqlColumn> getColumns();
  public List<SqlIndex> getIndices();
  public T createEntity();
  public T reifyEntity(SqlResultSet result);
  public void flush();
  public void clear();

  public T getEntity(Object id);
  public T getEntityWhere(Sql whereClause);
  public List<T> getEntitiesFrom(Sql fromClause);
  public List<T> getEntitiesFrom(String entityTableAlias, Sql fromClause);
  public List<T> getEntitiesWhere(Sql whereClause);
  public int countWhere(Sql whereClause);
  public boolean existsWhere(Sql whereClause);
  public int count(Sql sql);
  public boolean exists(Sql sql);

  public void query(Sql sql, Consumer<SqlResultSet> rowConsumer);
  public <R> R query(Sql sql, Function<SqlResultSet, R> mapper);
}
