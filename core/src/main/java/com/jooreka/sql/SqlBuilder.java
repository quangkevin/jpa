package com.jooreka.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SqlBuilder {
  private StringBuilder template;
  private List<Object> bindings;

  public SqlBuilder() {
    this.template = new StringBuilder();
    this.bindings = new ArrayList<>();
  }

  public SqlBuilder add(String template) {
    this.template.append(template);

    return this;
  }

  public SqlBuilder add(String template, Sql statement) {
    this.template.append(template);
    add(statement);

    return this;
  }

  public SqlBuilder add(String template, Collection<?> bindings) {
    this.template.append(template);
    this.bindings.addAll(bindings);

    return this;
  }

  public SqlBuilder add(String template, Object arg) {
    this.template.append(template);
    this.bindings.add(arg);

    return this;
  }

  public SqlBuilder add(String template, Object arg1, Object arg2) {
    this.template.append(template);
    this.bindings.add(arg1);
    this.bindings.add(arg2);

    return this;
  }

  public SqlBuilder add(String template, Object... args) {
    this.template.append(template);
    for (Object i : args) {
      this.bindings.add(i);
    }

    return this;
  }

  public SqlBuilder addIn(String template, Collection<?> bindings) {
    this.template
      .append(template)
      .append(" in ("
	      + bindings.stream().map(x -> "?").collect(Collectors.joining(", "))
	      + ")");
    this.bindings.addAll(bindings);

    return this;
  }

  public SqlBuilder addIfNotNull(String template, Object arg) {
    if (arg != null) {
      this.template.append(template);
      this.bindings.add(arg);
    }

    return this;
  }

  public SqlBuilder addIfNotEmpty(String template, Collection<?> bindings) {
    if (!bindings.isEmpty()) {
      add(template, bindings);
    }

    return this;
  }

  public SqlBuilder addInIfNotEmpty(String template, Collection<?> bindings) {
    if (!bindings.isEmpty()) {
      addIn(template, bindings);
    }

    return this;
  }

  public SqlBuilder add(Sql sql) {
    this.template.append(sql.getTemplate());
    this.bindings.addAll(sql.getBindings());

    return this;
  }

  public Sql build() {
    return new BasicSql(template.toString(), Collections.unmodifiableList(flattenBindings(bindings)));
  }

  private List<?> flattenBindings(List<?> bindings) {
    if (bindings.isEmpty()) return bindings;

    List<Object> flatten = new ArrayList<>(bindings.size());

    flattenBindings(bindings, flatten);

    return flatten;
  }

  private void flattenBindings(Collection<?> bindings, List<Object> flatten) {
    for (Object val : bindings) {
      if (val instanceof Collection<?>) {
        flattenBindings(((Collection<?>) val), flatten);
      } else {
        flatten.add(val);
      }
    }
  }
}
