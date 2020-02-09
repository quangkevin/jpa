package com.jooreka.sql;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class BasicSqlResultSet implements SqlResultSet {
  private int columnIndex;
  private ResultSet rs;

  public BasicSqlResultSet(ResultSet rs) {
    this(rs, 0);
  }

  public BasicSqlResultSet(ResultSet rs, int columnIndex) {
    this.rs = rs;
    this.columnIndex = columnIndex;
  }

  @Override
  public int getColumnIndex() {
    return columnIndex;
  }

  @Override
  public void setColumnIndex(int index) {
    this.columnIndex = index;
  }

  @Override
  public boolean next() {
    try {
      return rs.next();
    } catch (SQLException e) {
      throw new SqlException(e);
    }
  }

  @Override
  public Optional<Byte> nextByte() {
    try {
      byte result = rs.getByte(++columnIndex);
      return rs.wasNull() ? Optional.empty() : Optional.of(result);
    } catch (SQLException e) {
      throw new SqlException(e);
    }
  }

  @Override
  public Optional<Boolean> nextBoolean() {
    try {    
      boolean result = rs.getBoolean(++columnIndex);
      return rs.wasNull() ? Optional.empty() : Optional.of(result);
    } catch (SQLException e) {
      throw new SqlException(e);
    }
  }

  @Override
  public Optional<Integer> nextInteger() {
    try {
      int result = rs.getInt(++columnIndex);
      return rs.wasNull() ? Optional.empty() : Optional.of(result);
    } catch (SQLException e) {
      throw new SqlException(e);
    }
  }

  @Override
  public Optional<Long> nextLong() {
    try {
      long result = rs.getLong(++columnIndex);
      return rs.wasNull() ? Optional.empty() : Optional.of(result);
    } catch (SQLException e) {
      throw new SqlException(e);
    }
  }

  @Override
  public Optional<Float> nextFloat() {
    try {
      float result = rs.getFloat(++columnIndex);
      return rs.wasNull() ? Optional.empty() : Optional.of(result);
    } catch (SQLException e) {
      throw new SqlException(e);
    }
  }

  @Override
  public Optional<Double> nextDouble() {
    try {
      double result = rs.getDouble(++columnIndex);
      return rs.wasNull() ? Optional.empty() : Optional.of(result);
    } catch (SQLException e) {
      throw new SqlException(e);
    }
  }

  @Override
  public Optional<String> nextString() {
    try {
      String result = rs.getString(++columnIndex);
      return rs.wasNull() || result == null ? Optional.empty() : Optional.of(result);
    } catch (SQLException e) {
      throw new SqlException(e);
    }
  }

  @Override
  public Optional<Blob> nextBlob() {
    try {
      Blob result = rs.getBlob(++columnIndex);
      return rs.wasNull() || result == null ? Optional.empty() : Optional.of(result);
    } catch (SQLException e) {
      throw new SqlException(e);
    }
  }

  @Override
  public Optional<byte[]> nextBytes() {
    try {
      byte[] result = rs.getBytes(++columnIndex);
      return rs.wasNull() || result == null ? Optional.empty() : Optional.of(result);
    } catch (SQLException e) {
      throw new SqlException(e);
    }
  }

  @Override
  public Optional<Clob> nextClob() {
    try {
      Clob result = rs.getClob(++columnIndex);
      return rs.wasNull() || result == null ? Optional.empty() : Optional.of(result);
    } catch (SQLException e) {
      throw new SqlException(e);
    }
  }
}
