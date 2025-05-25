package ua.gorobeos.contextor.context.exceptions;

public class UnresolvableDependencyException extends RuntimeException {

  public UnresolvableDependencyException(String message) {
    super(message);
  }

  public UnresolvableDependencyException(String message, Throwable cause) {
    super(message, cause);
  }
}
