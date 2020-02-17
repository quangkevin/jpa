package com.jooreka.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Consumer;

public final class BasicSqlSession implements SqlSession {
  private final SqlConfig config;
  private final Map<String, SqlConnectionSession> connectionSessionMap;

  public BasicSqlSession(SqlConfig config) {
    this.config = config;
    this.connectionSessionMap = new HashMap<>();
  }
  
  @Override public SqlConfig getConfig() { return config; }

  @Override
  public SqlConnectionSession getConnectionSession(String databaseName) {
    return connectionSessionMap.computeIfAbsent(databaseName, (db) -> new BasicSqlConnectionSession(this, db));
  }

  @Override
  public void keepAlive() {
    forEachConnectSession(SqlConnectionSession::keepAlive);
  }

  @Override
  public void save() {
    forEachConnectSession(SqlConnectionSession::save);
  }

  @Override
  public void clear() {
    forEachConnectSession(SqlConnectionSession::clear);
  }

  @Override
  public void close() {
    forEachConnectSession(SqlConnectionSession::close);
  }

  private void forEachConnectSession(Consumer<SqlConnectionSession> consumer) {
    List<Exception> exceptions = new ArrayList<>();

    for (SqlConnectionSession session : connectionSessionMap.values()) {
      try {
	consumer.accept(session);
      } catch (Exception e) {
	exceptions.add(e);
      }
    }

    if (!exceptions.isEmpty()) {
      SqlException exception = new SqlException();
      exceptions.forEach(e -> exception.addSuppressed(e));

      throw exception;
    }
  }
}
