package com.jooreka.sql;

public class SqlException extends RuntimeException {
  public interface SqlExecutor {
    public void execute() throws Exception;
  }

  public interface SqlSupplier<T> {
    public T supply() throws Exception;
  }  
  
  public static void wrap(SqlExecutor executor) {
    try {
      executor.execute();
      
    } catch (SqlException e) {
      throw e;
      
    } catch (Exception e) {
      throw new SqlException(e);
    }
  }

  public static <T> T wrap(SqlSupplier<T> supplier) {
    try {
      return supplier.supply();
      
    } catch (SqlException e) {
      throw e;
      
    } catch (Exception e) {
      throw new SqlException(e);
    }
  }

  public SqlException() {
  }

  public SqlException(String message) {
    super(message);
  }

  public SqlException(String message, Object... args) {
    super(args.length == 0 ? message : String.format(message, args));
  }

  public SqlException(Exception exception) {
    super(exception);
  }

  public SqlException(Exception exception, String message) {
    super(message, exception);
  }

  public SqlException(Exception exception, String message, Object... args) {
    super(args.length == 0 ? message : String.format(message, args), exception);
  }    
}
