package ua.gorobeos.contextor.context.exceptions;

public class ElementDefinitionCreationException extends RuntimeException {

  public ElementDefinitionCreationException(String message) {
    super(message);
  }

  public ElementDefinitionCreationException(String message, Throwable cause) {
    super(message, cause);
  }
}
