package com.jooreka.sql;

import java.sql.Clob;

public interface SqlRow {
  public int getColumnIndex();
  public void setColumnIndex(int index);

  public Byte nextOptionalByte();  
  public Boolean nextOptionalBoolean();
  public Integer nextOptionalInt();
  public Long nextOptionalLong();
  public Float nextOptionalFloat();    
  public Double nextOptionalDouble();

  public byte nextByte();  
  public boolean nextBoolean();
  public int nextInt();
  public long nextLong();
  public float nextFloat();    
  public double nextDouble();
  
  public String nextString();
  public Clob nextClob();
  public byte[] nextBytes();  
}
