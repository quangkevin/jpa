package com.jooreka.sql;

public interface SqlSession {
  public SqlConfig getConfig();
  public SqlConnectionSession getConnectionSession(String databaseName);
  public void keepAlive();
  public void save();
  public void clear();
  public void close();
}
