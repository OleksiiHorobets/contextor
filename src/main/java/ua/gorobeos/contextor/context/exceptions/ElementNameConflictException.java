package ua.gorobeos.contextor.context.exceptions;

public class ElementNameConflictException extends RuntimeException{

  public ElementNameConflictException(String message) {
    super(message);
  }

  public ElementNameConflictException(String message, Throwable cause) {
    super(message, cause);
  }
}
