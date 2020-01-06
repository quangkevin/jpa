package com.jooreka.sql;

import java.util.function.Supplier;

public interface SqlSequence {
  public Supplier<Long> next();
}
