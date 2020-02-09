package com.jooreka.jpa.mysql;

import com.jooreka.sql.SqlConfig;
import com.jooreka.sql.SqlException;
import com.jooreka.sql.SqlSequence;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class MysqlConfig implements SqlConfig {
  public static class Builder {
    private Map<String, DataSource> dataSourceMap = new HashMap<>();

    public Builder setDataSource(String databaseName, DataSource dataSource) {
      dataSourceMap.put(databaseName, dataSource);
      return this;
    }

    public MysqlConfig build() {
      return new MysqlConfig(dataSourceMap);
    }
  }

  public static class DataSourceBuilder {
    HikariConfig config = new HikariConfig();

    public DataSourceBuilder setUrl(String url) {
      config.setJdbcUrl(url);
      return this;
    }

    public DataSourceBuilder setUserName(String userName) {
      config.setUsername(userName);
      return this;
    }

    public DataSourceBuilder setPassword(String password) {
      config.setPassword(password);
      return this;
    }

    public DataSourceBuilder setCachePreparedStatements(boolean cache) {
      config.addDataSourceProperty("cachePrepStmts", String.valueOf(cache));
      return this;
    }

    public DataSourceBuilder setPreparedStatementCacheSqlLimit(int limit) {
      config.addDataSourceProperty("prepStmtCacheSqlLimit", String.valueOf(limit));
      return this;
    }

    public DataSourceBuilder setPreparedStatementCacheSize(int cacheSize) {
      config.addDataSourceProperty("prepStmtCacheSize", String.valueOf(cacheSize));
      return this;
    }

    public DataSource build() {
      return new HikariDataSource(config);
    }
  }

  private Map<String, DataSource> dataSourceMap;

  private MysqlConfig(Map<String, DataSource> dataSourceMap) {
    this.dataSourceMap = dataSourceMap;
  }

  public Connection createConnection(String databaseName) {
    DataSource ds = dataSourceMap.get(databaseName);

    if (ds == null) {
      throw new SqlException("No DataSource configured for database " + databaseName);
    }

    return SqlException.wrap(() -> ds.getConnection());
  }
  
  public SqlSequence createSequence(String databaseName, String tableName) {
    return new MysqlSequence(this, databaseName, tableName);
  }
}
