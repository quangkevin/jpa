package com.jooreka.sql.processor.internal;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public class ErrorReporter {
  private final Messager messager;
  private int errorCount;

  public ErrorReporter(Messager messager) {
    this.messager = messager;
  }

  public void note(Element e, String msg) {
    messager.printMessage(Diagnostic.Kind.NOTE, msg, e);
  }

  public void warn(Element e, String msg) {
    messager.printMessage(Diagnostic.Kind.WARNING, msg, e);
  }

  public void err(Element e, String msg) {
    messager.printMessage(Diagnostic.Kind.ERROR, msg, e);
    ++errorCount;
  }

  public void err(Element e, String msg, Object...args) {
    messager.printMessage(Diagnostic.Kind.ERROR,
			  args.length == 0 ? msg : String.format(msg, args),
			  e);
    ++errorCount;
  }  

  public int getErrorCount() {
    return errorCount;
  }
}
