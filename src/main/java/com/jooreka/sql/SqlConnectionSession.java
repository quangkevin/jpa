package com.jooreka.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.util.function.Function;

public interface SqlConnectionSession {
  public SqlSession getSession();
  public Connection getConnection();
  public <T extends SqlTable<? extends SqlEntity>> T getTable(Class<T> tableClass, Function<SqlConnectionSession, T> supplier);
  public void addCommand(Runnable command);
  public boolean isAlive();
  public void keepAlive();
  public void save();  
  public void clear();
  public void close();
}
