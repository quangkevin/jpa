package com.jooreka.sql.processor;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

class ErrorReporter {
  private final Messager messager;
  private int errorCount;

  ErrorReporter(Messager messager) {
    this.messager = messager;
  }

  void note(String msg, Element e) {
    messager.printMessage(Diagnostic.Kind.NOTE, msg, e);
  }

  void warn(String msg, Element e) {
    messager.printMessage(Diagnostic.Kind.WARNING, msg, e);
  }

  void err(String msg, Element e) {
    messager.printMessage(Diagnostic.Kind.ERROR, msg, e);
    ++errorCount;
  }

  int getErrorCount() {
    return errorCount;
  }
}
