package com.jooreka.sql.processor;

import java.util.Arrays;
import java.util.stream.Collectors;

class Converter {
  public static String camelCaseToSnakeCase(String val) {
    if (val == null || val.length() < 2) return val;

    return val.substring(0, 1) + val.substring(1).replaceAll("([A-Z])", "_$1");
  }

  public static String snakeCaseToCamelCase(String val) {
    if (val == null) return val;
    if (val.length() == 1) return val.toLowerCase();

    val = Arrays
      .asList(val.toLowerCase().split("_"))
      .stream()
      .map(x -> (x.length() < 2
		 ? x.toUpperCase()
		 : (x.substring(0, 1).toUpperCase() + x.substring(1))))
      .collect(Collectors.joining(""));

    return val.substring(0, 1).toLowerCase() + val.substring(1);
  }
}
