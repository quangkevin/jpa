package com.jooreka.sql;

public interface SqlEntity {
  public boolean isPersisted();
  public void flush();
  public void delete();
  public void markDirty();
}
