package ua.gorobeos.contextor.context.exceptions;

public class PropertiesLoadException extends RuntimeException {

  public PropertiesLoadException(String message) {
    super(message);
  }

  public PropertiesLoadException(String message, Throwable cause) {
    super(message, cause);
  }
}
