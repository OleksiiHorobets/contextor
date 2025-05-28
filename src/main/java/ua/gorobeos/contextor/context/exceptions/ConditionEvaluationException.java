package ua.gorobeos.contextor.context.exceptions;

public class ConditionEvaluationException extends RuntimeException {

  public ConditionEvaluationException(String message) {
    super(message);
  }

  public ConditionEvaluationException(String message, Throwable cause) {
    super(message, cause);
  }
}
