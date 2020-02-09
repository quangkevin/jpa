package com.jooreka.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractSqlTable<T extends SqlEntity> implements SqlTable<T> {

  public T getEntityWhere(SqlStatement whereClause) {
    List<T> result = getEntitiesWhere(whereClause);

    if (1 < result.size()) {
      throw new SqlException("Too many entities (%d) (WHERE: %s)",
			     result.size(),
			     whereClause);
    }

    return result.isEmpty() ? null : result.get(0);
  }  

  public List<T> getEntitiesFrom(SqlStatement fromClause) {
    return getEntitiesFrom(null, fromClause);
  }

  public List<T> getEntitiesFrom(String tableAlias, SqlStatement fromClause) {
    if (tableAlias == null) {
      tableAlias = getTableName();
    }

    final String entityTable = tableAlias;
    
    return queryList(new SqlStatementBuilder()
		     .add("SELECT " + getColumns().stream().map(x -> entityTable + "." + x.getName()))
		     .add(" FROM ", fromClause)
		     .build(),
		     rs -> reifyEntity(rs));
  }

  @Override public List<T> getEntitiesWhere(SqlStatement whereClause) {
    return queryList(new SqlStatementBuilder()
		     .add("SELECT " + getColumns().stream().map(x -> x.getName()))
		     .add(" FROM " + getTableName())
		     .add(" WHERE ", whereClause)
		     .build(),
		     rs -> reifyEntity(rs)
		     );
  }

  public int countWhere(SqlStatement whereClause) {
    return count(new SqlStatementBuilder()
		 .add("SELECT count(1)")
		 .add(" FROM " + getTableName())
		 .add(" WHERE ", whereClause)
		 .build());
  }

  public boolean existsWhere(SqlStatement whereClause) {
    return exists(new SqlStatementBuilder()
		  .add("SELECT 1")
		  .add(" FROM " + getTableName())
		  .add(" WHERE ", whereClause)
		  .build());
  }

  @Override
  public int count(SqlStatement sql) {
    return query(sql, rs -> rs.next() ? rs.nextInt().get() : 0);
  }
    
  @Override
  public boolean exists(SqlStatement query) {
    return query(query, rs -> { return rs.next(); });
  }

  public <R> List<R> queryList(SqlStatement sql, Function<SqlResultSet, R> function) {
    return query(sql, rs -> {
	List<R> result = new ArrayList<>();

	while (rs.next()) {
	  result.add(function.apply(rs));
	}

	return result;
      });
  }

  @Override public void query(SqlStatement sql, Consumer<SqlResultSet> consumer) {
    try (PreparedStatement statement = sql.toPreparedStatement(getSession());
         ResultSet rs = statement.executeQuery()) {
      consumer.accept(new BasicSqlResultSet(rs));
    } catch (SQLException e) {
      throw new SqlException(e);
    }
  }

  @Override public <R> R query(SqlStatement sql, Function<SqlResultSet, R> function) {
    try (PreparedStatement statement = sql.toPreparedStatement(getSession());
         ResultSet rs = statement.executeQuery()) {
      return function.apply(new BasicSqlResultSet(rs));
    } catch (SQLException e) {
      throw new SqlException(e);
    }
  }
}
