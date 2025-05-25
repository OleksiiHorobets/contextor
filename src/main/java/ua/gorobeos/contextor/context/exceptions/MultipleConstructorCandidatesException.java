package ua.gorobeos.contextor.context.exceptions;

public class MultipleConstructorCandidatesException extends RuntimeException {

  public MultipleConstructorCandidatesException(String message) {
    super(message);
  }

  public MultipleConstructorCandidatesException(String message, Throwable cause) {
    super(message, cause);
  }
}

