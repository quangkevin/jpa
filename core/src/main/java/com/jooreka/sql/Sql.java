package com.jooreka.sql;

import java.sql.PreparedStatement;
import java.util.List;

public interface Sql {
  public String getTemplate();
  public List<?> getBindings();

  public PreparedStatement toPreparedStatement(SqlConnectionSession connectionSession);
}
