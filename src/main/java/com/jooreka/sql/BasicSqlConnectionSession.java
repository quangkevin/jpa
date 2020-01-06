package com.jooreka.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BasicSqlConnectionSession implements SqlConnectionSession {
  private static final Logger LOGGER = LoggerFactory.getLogger(BasicSqlConnectionSession.class);

  private SqlSession session;  
  private String databaseName;
  private Connection connection;

  private Map<Class<?>, SqlTable<?>> tables;
  private List<Runnable> commands;

  public BasicSqlConnectionSession(SqlSession session, String databaseName) {
    this.session = session;    
    this.databaseName = databaseName;
    this.connection = session.getConfig().createConnection(databaseName);
    
    this.tables = new HashMap<>();
    this.commands = new ArrayList<>();
  }

  @Override
  public SqlSession getSession() { return session; }

  @Override
  public Connection getConnection() { return connection; }

  @Override
  public <T extends SqlTable<? extends SqlEntity>> T getTable(Class<T> tableClass, Function<SqlConnectionSession, T> supplier) {
    return tableClass.cast(tables.computeIfAbsent(tableClass, (key) -> supplier.apply(this)));
  }

  @Override
  public void addCommand(Runnable command) {
    this.commands.add(command);
  }

  @Override
  public void save() {
    flushTables();
    flushCommands();

    SqlException.wrap(() -> this.connection.commit());    
  }

  @Override
  public void clear() {
    for (SqlTable<?> table : this.tables.values()) {
      table.clear();
    }

    commands.clear();
    
    SqlException.wrap(() -> connection.rollback());
  }

  @Override public void close() {
    closeFaultTolerant();
  }

  @Override public boolean isAlive() {
    try (PreparedStatement statement = connection.prepareStatement("select 1 from dual");
	 ResultSet rows = statement.executeQuery()) {
      return true;
      
    } catch (SQLException e) {
      return false;
    }
  }

  @Override 
  public void keepAlive() {
    try (PreparedStatement statement = connection.prepareStatement("select 1 from dual");
	 ResultSet rows = statement.executeQuery()) {
      
      // nothing to do left
      
    } catch (SQLException e) {
      rollbackFaultTolerant();
      closeFaultTolerant();
      connection = session.getConfig().createConnection(databaseName);
    } 
  }  

  private void flushTables() {
    List<SqlTable<?>> sortedTables = new ArrayList<>(tables.values());
    Collections.sort(sortedTables, (a, b) -> a.getTableName().compareToIgnoreCase(b.getTableName()));
    
    for (SqlTable<?> table : sortedTables) {
      table.flush();
    }
  }

  private void flushCommands() {
    for (Runnable command : commands) {
      command.run();
    }

    commands.clear();
  }

  private void rollbackFaultTolerant() {
    try {
      connection.rollback();
    } catch (Exception e) {}
  }

  private void closeFaultTolerant() {
    try {
      connection.close();
    } catch (Exception e) {
    } finally {
      connection = null;
    }    
  }
}
