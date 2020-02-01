package com.jooreka.sql;

public class BasicSqlColumn implements SqlColumn {
  public static class Builder {
    private String name;
    private int type;
    private Integer length;
    private String definition;
    private boolean unique;
    private boolean nullable;
    private boolean insertable;
    private boolean updatable;
    private boolean isId;

    public Builder withName(String name) { this.name = name; return this; }
    public Builder withType(int type) { this.type = type; return this; }
    public Builder withLength(Integer length) { this.length = length; return this; }
    public Builder withDefinition(String definition) { this.definition = definition; return this; }
    public Builder withUnique(boolean unique) { this.unique = unique; return this; }
    public Builder withNullable(boolean nullable) { this.nullable = nullable; return this; }
    public Builder withInsertable(boolean insertable) { this.insertable = insertable; return this; }
    public Builder withUpdatable(boolean updatable) { this.updatable = updatable; return this; }
    public Builder withIsId(boolean isId) { this.isId = isId; return this; }
    
    public SqlColumn build() {
      return new BasicSqlColumn(name, type, length, definition, unique, nullable, insertable, updatable, isId);
    }
  }
  
  private final String name;
  private final int type;
  private final Integer length;
  private final String definition;
  private final boolean unique;
  private final boolean nullable;
  private final boolean insertable;
  private final boolean updatable;
  private final boolean isId;

  private BasicSqlColumn(
			 String name,
			 int type,
			 Integer length,
			 String definition,
			 boolean unique,
			 boolean nullable,
			 boolean insertable,
			 boolean updatable,
			 boolean isId) {
    this.name = name;
    this.type = type;
    this.length = length;
    this.definition = definition;
    this.unique = unique;
    this.nullable = nullable;
    this.insertable = insertable;
    this.updatable = updatable;
    this.isId = isId;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getType() {
    return type;
  }

  @Override
  public Integer getLength() {
    return length;
  }

  @Override
  public String getDefinition() {
    return definition;
  }

  @Override
  public boolean isUnique() {
    return unique;
  }

  @Override
  public boolean isNullable() {
    return nullable;
  }

  @Override
  public boolean isInsertable() {
    return insertable;
  }

  @Override
  public boolean isUpdatable() {
    return updatable;
  }

  @Override
  public boolean isId() {
    return isId;
  }
}
