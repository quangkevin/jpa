package com.jooreka.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException; 
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class MysqlSequence implements SqlSequence {
  private SqlConfig config;
  private String databaseName;
  private String tableName;
  private Token lastToken;

  public MysqlSequence(SqlConfig config, String databaseName, String tableName) {
    this.config = config;
    this.databaseName = databaseName;
    this.tableName = tableName;
    this.lastToken = new Token(this, null, 0);
  }

  public synchronized Token next() {
    return (lastToken = new Token(this, lastToken, lastToken.offset + 1)); 
  }

  private synchronized long generate(long offset) {
    long result;

    try (Connection connection = config.createConnection(databaseName)) {
      try (PreparedStatement statement = connection.prepareStatement(String.format("UPDATE %s_seq SET id = last_insert_id(id + %d)",
										   tableName, lastToken.offset))) {
	statement.executeUpdate();
      }

      try (PreparedStatement statement = connection.prepareStatement("SELECT last_insert_id()");
	   ResultSet rs = statement.executeQuery()) {
	if (!rs.next()) {
	  throw new SqlException("Failed to generate sequence for " + databaseName + "." + tableName);
	}

	long firstId = rs.getLong(1) - lastToken.offset;

	while (0 < lastToken.offset) {
	  lastToken.id = firstId + lastToken.offset;
	  lastToken = lastToken.previous;
	}

	result = firstId + offset;
      }

      connection.commit();
	
    } catch (SQLException e) {
      throw new SqlException(e, "Failed to generate sequence for " + databaseName + "." + tableName);
    }

    return result;
  }

  private static class Token implements Supplier<Long> {
    private final MysqlSequence sequence;
    private final Token previous; 
    private final long offset;
    private Long id;

    public Token(MysqlSequence sequence, Token previous, long offset) {
      this.sequence = sequence;
      this.previous = previous;
      this.offset = offset;
    }

    @Override public Long get() {
      if (id == null) {
	id = sequence.generate(offset);
      }

      return id;
    }
  }
}
