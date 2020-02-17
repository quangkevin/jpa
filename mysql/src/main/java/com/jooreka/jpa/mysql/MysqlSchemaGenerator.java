package com.jooreka.jpa.mysql;

import com.jooreka.sql.SqlColumn;
import com.jooreka.sql.SqlConfig;
import com.jooreka.sql.SqlConnectionSession;
import com.jooreka.sql.SqlEntity;
import com.jooreka.sql.SqlIndex;
import com.jooreka.sql.SqlSequence;
import com.jooreka.sql.SqlSession;
import com.jooreka.sql.SqlTable;
import com.jooreka.sql.SqlTableImplementations;

import java.sql.Array;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MysqlSchemaGenerator {
  //public String generate(Class<? extends SqlEntity>... entityClasses) {
  //  SchemaSession session = new SchemaSession();
  //
  //  return Arrays.asList(entityClasses)
  //    .stream()
  //    .map(session::getTable)
  //    .map(table -> createTable(table))
  //    .collect(Collectors.joining("\n\n"));
  //}

  public String createTable(SqlTable<?> table) {
    StringBuilder buf = new StringBuilder();
    buf
      .append("CREATE TABLE ")
      .append(table.getTableName())
      .append(" (");

    boolean first = true;

    for (SqlColumn column : table.getColumns()) {
      if (!first) buf.append(",");

      buf.append("\n ").append(column.getDefinition());
      first = false;
    }

    for (SqlIndex index : table.getIndices()) {
      if (!first) buf.append(",\n");

      buf
        .append(" INDEX ")
        .append(index.getName())
        .append(" (")
        .append(index.getColumnNames().stream().collect(Collectors.joining(", ")))
        .append(")");

      if (index.isUnique()) {
        buf.append(" UNIQUE");
      }

      first = false;
    }

    buf.append("\n) engine=InnoDb;");

    return buf.toString();
  }


  private static class SchemaSession implements SqlSession {
    private SqlTableImplementations tableImplementations = new SqlTableImplementations();

    @Override
    public SqlConfig getConfig() {
      return new SchemaConfig();
    }

    @Override
    public SqlConnectionSession getConnectionSession(String databaseName) {
      return new SchemaSqlConnectionSession(this);
    }

    @Override
    public <T extends SqlEntity> SqlTable<T> getTable(Class<T> entityClass) {
      return tableImplementations.implement(entityClass).apply(this);
    }

    @Override
    public void keepAlive() {
    }

    @Override
    public void save() {
    }

    @Override
    public void clear() {
    }

    @Override
    public void close() {
    }
  }

  private static class SchemaConfig implements SqlConfig {
    @Override
    public Connection createConnection(String s) {
      return null;
    }

    @Override
    public SqlSequence createSequence(String s, String s1) {
      return null;
    }
  }

  private static class SchemaSqlConnectionSession implements SqlConnectionSession {
    private SqlSession session;

    private SchemaSqlConnectionSession(SqlSession session) {
      this.session = session;
    }

    @Override
    public SqlSession getSession() {
      return session;
    }

    @Override
    public Connection getConnection() {
      return null;
    }

    @Override
    public <T extends SqlTable<? extends SqlEntity>> T getTable(Class<T> tableClass, Function<SqlConnectionSession, T> supplier) {
      return supplier.apply(this);
    }

    @Override
    public void addCommand(Runnable command) {
    }

    @Override
    public boolean isAlive() {
      return false;
    }

    @Override
    public void keepAlive() {
    }

    @Override
    public void save() {
    }

    @Override
    public void clear() {
    }

    @Override
    public void close() {
    }
  }
}
