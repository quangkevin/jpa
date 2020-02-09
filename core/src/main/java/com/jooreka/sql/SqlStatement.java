package com.jooreka.sql;

import java.sql.PreparedStatement;
import java.util.List;

public interface SqlStatement {
  public String getSql();
  public List<?> getBindings();

  public PreparedStatement toPreparedStatement(SqlConnectionSession connectionSession);
}
