package com.jooreka.sql;

import java.sql.Connection;

public interface SqlConfig {
  public Connection createConnection(String databaseName);  
  public SqlSequence createSequence(String databaseName, String tableName);
}
