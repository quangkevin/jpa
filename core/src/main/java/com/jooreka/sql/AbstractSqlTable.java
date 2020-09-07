package com.jooreka.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractSqlTable<T extends SqlEntity> implements SqlTable<T> {

  public T getEntityWhere(Sql whereClause) {
    List<T> result = getEntitiesWhere(whereClause);

    if (1 < result.size()) {
      throw new SqlException("Too many entities (%d) (WHERE: %s)",
			     result.size(),
			     whereClause);
    }

    return result.isEmpty() ? null : result.get(0);
  }

  public List<T> getEntitiesFrom(Sql fromClause) {
    return getEntitiesFrom(null, fromClause);
  }

  public List<T> getEntitiesFrom(String tableAlias, Sql fromClause) {
    if (tableAlias == null) {
      tableAlias = getTableName();
    }

    final String entityTable = tableAlias;

    return queryList(new SqlBuilder()
		     .add("SELECT " + getColumns().stream().map(x -> entityTable + "." + x.getName()).collect(Collectors.joining(",")))
		     .add(" FROM ", fromClause)
		     .build(),
		     rs -> reifyEntity(rs));
  }

  @Override public List<T> getEntitiesWhere(Sql whereClause) {
    return queryList(new SqlBuilder()
		     .add("SELECT " + getColumns().stream().map(x -> x.getName()).collect(Collectors.joining(",")))
		     .add(" FROM " + getTableName())
		     .add(" WHERE ", whereClause)
		     .build(),
		     rs -> reifyEntity(rs)
		     );
  }

  public int countWhere(Sql whereClause) {
    return count(new SqlBuilder()
		 .add("SELECT count(1)")
		 .add(" FROM " + getTableName())
		 .add(" WHERE ", whereClause)
		 .build());
  }

  public boolean existsWhere(Sql whereClause) {
    return exists(new SqlBuilder()
		  .add("SELECT 1")
		  .add(" FROM " + getTableName())
		  .add(" WHERE ", whereClause)
		  .build());
  }

  @Override
  public int count(Sql sql) {
    return query(sql, rs -> rs.next() ? rs.nextInteger().get() : 0);
  }

  @Override
  public boolean exists(Sql query) {
    return query(query, rs -> { return rs.next(); });
  }

  public <R> List<R> queryList(Sql sql, Function<SqlResultSet, R> function) {
    return query(sql, rs -> {
	List<R> result = new ArrayList<>();

	while (rs.next()) {
	  result.add(function.apply(rs));
	}

	return result;
      });
  }

  @Override public void query(Sql sql, Consumer<SqlResultSet> consumer) {
    try (PreparedStatement statement = sql.toPreparedStatement(getSession());
         ResultSet rs = statement.executeQuery()) {
      consumer.accept(new BasicSqlResultSet(rs));
    } catch (SQLException e) {
      throw new SqlException(e);
    }
  }

  @Override public <R> R query(Sql sql, Function<SqlResultSet, R> function) {
    try (PreparedStatement statement = sql.toPreparedStatement(getSession());
         ResultSet rs = statement.executeQuery()) {
      return function.apply(new BasicSqlResultSet(rs));
    } catch (SQLException e) {
      throw new SqlException(e);
    }
  }
}
