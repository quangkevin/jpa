package com.jooreka.sql;

import java.util.stream.Collectors;

public class MysqlSchemaGenerator {
  public String createTable(SqlTable<?> table) {
    StringBuilder buf = new StringBuilder();
    buf
      .append("CREATE TABLE ")
      .append(table.getTableName())
      .append(" (");

    boolean first = true;

    for (SqlColumn column : table.getColumns()) {
      if (!first) buf.append(",");

      buf.append("\n ").append(column.getDefinition());
      first = false;
    }

    for (SqlIndex index : table.getIndices()) {
      if (!first) buf.append(",\n");

      buf
	.append(" INDEX ")
	.append(index)
	.append(" (")
	.append(index.getColumnNames().stream().collect(Collectors.joining(", ")))
	.append(")");

      if (index.isUnique()) {
	buf.append(" UNIQUE");
      }

      first = false;
    }

    buf.append("\n) engine=InnoDb");

    return buf.toString();
  }
}
