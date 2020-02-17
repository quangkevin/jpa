package com.jooreka.sql.processor.internal;

import javax.lang.model.element.Element;

public class InvalidEntityException extends RuntimeException {
  private Element element;

  public InvalidEntityException(Element element, String message) {
    super(message);
    this.element = element;
  }

  public InvalidEntityException(Element element, String message, Object... args) {
    super(0 < args.length ? String.format(message, args) : message);

    this.element = element;
  }

  public Element getElement() {
    return element;
  }
}
