package com.jooreka.sql;

public interface SqlSession {
  public SqlConfig getConfig();
  public SqlConnectionSession getConnectionSession(String databaseName);
  public <T extends SqlEntity> SqlTable<T> getTable(Class<T> entityClass);
  public void keepAlive();
  public void save();
  public void clear();
  public void close();
}
