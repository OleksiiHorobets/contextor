package ua.gorobeos.contextor.context.exceptions;

public class ContainerInitException extends RuntimeException {

  public ContainerInitException(String message) {
    super(message);
  }

  public ContainerInitException(String message, Throwable cause) {
    super(message, cause);
  }
}
