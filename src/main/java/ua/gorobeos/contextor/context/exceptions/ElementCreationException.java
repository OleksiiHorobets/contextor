package ua.gorobeos.contextor.context.exceptions;

public class ElementCreationException extends RuntimeException {

  public ElementCreationException(String message) {
    super(message);
  }

  public ElementCreationException(String message, Throwable cause) {
    super(message, cause);
  }
}
