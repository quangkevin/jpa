package com.jooreka.sql;

public interface SqlTable<T extends SqlEntity> {
  public SqlConnectionSession getSession();
  public String getTableName();
  public String getDatabaseName();
  public T create();
  public T reify(SqlRow row);
  public void flush();
  public void clear();
}
