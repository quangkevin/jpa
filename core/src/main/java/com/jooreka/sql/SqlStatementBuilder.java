package com.jooreka.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SqlStatementBuilder {
  private StringBuilder sqlString;
  private List<Object> bindings;

  public SqlStatementBuilder() {
    this.sqlString = new StringBuilder();
    this.bindings = new ArrayList<>();
  }

  public SqlStatementBuilder add(String sql) {
    this.sqlString.append(sql);

    return this;
  }

  public SqlStatementBuilder add(String sql, SqlStatement statement) {
    this.sqlString.append(sql);
    add(statement);

    return this;
  }  

  public SqlStatementBuilder add(String sql, Collection<?> bindings) {
    this.sqlString.append(sql);
    this.bindings.addAll(bindings);

    return this;
  }

  public SqlStatementBuilder add(String sql, Object arg) {
    this.sqlString.append(sql);
    this.bindings.add(arg);

    return this;
  }

  public SqlStatementBuilder add(String sql, Object arg1, Object arg2) {
    this.sqlString.append(sql);
    this.bindings.add(arg1);
    this.bindings.add(arg2);

    return this;
  }

  public SqlStatementBuilder add(String sql, Object... args) {
    this.sqlString.append(sql);
    for (Object i : args) {
      this.bindings.add(i);
    }

    return this;
  }

  public SqlStatementBuilder addIn(String sql, Collection<?> bindings) {
    this.sqlString
      .append(sql)
      .append(" in ("
	      + bindings.stream().map(x -> "?").collect(Collectors.joining(", "))
	      + ")");
    this.bindings.addAll(bindings);

    return this;
  }

  public SqlStatementBuilder addIfNotNull(String sql, Object arg) {
    if (arg != null) {
      this.sqlString.append(sql);
      this.bindings.add(arg);
    }

    return this;
  }

  public SqlStatementBuilder addIfNotEmpty(String sql, Collection<?> bindings) {
    if (!bindings.isEmpty()) {
      add(sql, bindings);
    }

    return this;
  }

  public SqlStatementBuilder addInIfNotEmpty(String sql, Collection<?> bindings) {
    if (!bindings.isEmpty()) {
      addIn(sql, bindings);
    }

    return this;
  }

  public SqlStatementBuilder add(SqlStatement statement) {
    this.sqlString.append(statement.getSql());
    this.bindings.addAll(statement.getBindings());

    return this;
  }

  public SqlStatement build() {
    return new BasicSqlStatement(sqlString.toString(), Collections.unmodifiableList(flattenBindings(bindings)));
  }

  private List<?> flattenBindings(List<?> bindings) {
    if (bindings.isEmpty()) return bindings;

    List<Object> flatten = new ArrayList<>(bindings.size());

    flattenBindings(bindings, flatten);

    return flatten;
  }

  private void flattenBindings(Collection<?> bindings, List<Object> flatten) {
    for (Object val : bindings) {
      if (val instanceof Collection<?>) {
        flattenBindings(((Collection<?>) val), flatten);
      } else {
        flatten.add(val);
      }
    }
  }
}
