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
  public T getEntityWhere(SqlStatement whereClause);  
  public List<T> getEntitiesFrom(SqlStatement fromClause);
  public List<T> getEntitiesFrom(String entityTableAlias, SqlStatement fromClause);
  public List<T> getEntitiesWhere(SqlStatement whereClause);  
  public int countWhere(SqlStatement whereClause);
  public boolean existsWhere(SqlStatement whereClause);
  public int count(SqlStatement sql);
  public boolean exists(SqlStatement sql);
  
  public void query(SqlStatement sql, Consumer<SqlResultSet> rowConsumer);
  public <R> R query(SqlStatement sql, Function<SqlResultSet, R> mapper);
}
