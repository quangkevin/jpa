package com.jooreka.sql;

import java.sql.Blob;
import java.sql.Clob;
import java.util.Optional;

public interface SqlResultSet {
  public int getColumnIndex();
  public void setColumnIndex(int index);
  public boolean next();

  public Optional<Byte> nextByte();
  public Optional<Boolean> nextBoolean();
  public Optional<Integer> nextInteger();
  public Optional<Long> nextLong();
  public Optional<Float> nextFloat();
  public Optional<Double> nextDouble();
  public Optional<String> nextString();
  public Optional<Blob> nextBlob();
  public Optional<byte[]> nextBytes();
  public Optional<Clob> nextClob();
}
