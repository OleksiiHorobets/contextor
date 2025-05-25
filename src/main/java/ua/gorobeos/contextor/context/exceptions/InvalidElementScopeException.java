package ua.gorobeos.contextor.context.exceptions;

public class InvalidElementScopeException extends RuntimeException{

  public InvalidElementScopeException(String message) {
    super(message);
  }

  public InvalidElementScopeException(String message, Throwable cause) {
    super(message, cause);
  }
}
