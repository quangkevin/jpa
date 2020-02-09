package com.jooreka.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class BasicSqlStatement implements SqlStatement {
  private final String sql;
  private final List<?> bindings;

  public BasicSqlStatement(String sql, List<?> bindings) {
    this.sql = sql;
    this.bindings = bindings;
  }

  @Override public String getSql() { return sql; }
  @Override public List<?> getBindings() { return bindings; }

  @Override public String toString() {
    if (bindings.isEmpty()) return sql;

    StringBuilder buf = new StringBuilder();

    int beginIndex = 0;

    for (Object val : bindings) {
      int endIndex = sql.indexOf('?', beginIndex);

      if (endIndex == -1) {
        throw new SqlException("Bindings mismatched (SQL: %s) (Bindings: %d)", sql, bindings.size());
      }

      buf.append(sql.substring(beginIndex, endIndex));
      buf.append(String.valueOf(val));

      beginIndex++;
    }

    if ((beginIndex + 1) < sql.length()) {
      buf.append(sql.substring(beginIndex));
    }

    return buf.toString();
  }

  @Override public PreparedStatement toPreparedStatement(SqlConnectionSession connectionSession) {
    try {
      PreparedStatement statement = connectionSession.getConnection().prepareStatement(getSql());
      bind(statement);

      return statement;

    } catch (SQLException e) {
      throw new SqlException(e);
    }
  }

  private void bind(PreparedStatement statement)
    throws SQLException
  {
    for (int i = 0; i < bindings.size(); ++i) {
      Object value = bindings.get(i);

      if (value == null) {
        statement.setNull(i + 1, Types.NULL);

      } else if (value instanceof String) {
        statement.setString(i + 1, (String) value);

      } else if (value instanceof Boolean) {
        statement.setBoolean(i + 1, Boolean.TRUE.equals(value));

      } else if (value instanceof Float) {
        statement.setFloat(i + 1, (Float) value);

      } else if (value instanceof Double) {
        statement.setDouble(i + 1, (Double) value);

      } else if (value instanceof Integer) {
        statement.setInt(i + 1, (Integer) value);

      } else if (value instanceof Long) {
        statement.setLong(i + 1, (Long) value);

      } else if (value instanceof byte[]) {
        statement.setBytes(i + 1, (byte[]) value);
      }
    }
  }
}
