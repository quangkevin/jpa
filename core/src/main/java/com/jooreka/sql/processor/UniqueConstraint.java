package com.jooreka.sql.processor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Specifies that a unique constraint is to be included in
 * the generated DDL for a primary or secondary table.
 *
 * <pre>
 *    Example:
 *    &#064;Entity
 *    &#064;Table(
 *        name="EMPLOYEE",
 *        uniqueConstraints=
 *            &#064;UniqueConstraint(columnNames={"EMP_ID", "EMP_NAME"})
 *    )
 *    public class Employee { ... }
 * </pre>
 *
 */
@Target({ })
@Retention(SOURCE)
public @interface UniqueConstraint {
  /**
   * (Optional) Constraint name.  A provider-chosen name will be chosen
   * if a name is not specified.
   *
   */
  String name() default "";

  /**
   * (Required) An array of the column names that make up the constraint.
   */
  String[] columns();
}
