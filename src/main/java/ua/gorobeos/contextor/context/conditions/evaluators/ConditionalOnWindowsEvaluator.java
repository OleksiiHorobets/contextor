package ua.gorobeos.contextor.context.conditions.evaluators;

import lombok.extern.slf4j.Slf4j;
import ua.gorobeos.contextor.context.annotations.conditions.ConditionalOnWindows;
import ua.gorobeos.contextor.context.conditions.ConditionalContext;
import ua.gorobeos.contextor.context.conditions.ConditionalEvaluator;
import ua.gorobeos.contextor.context.utils.ReflectionUtils;

@Slf4j
public class ConditionalOnWindowsEvaluator implements ConditionalEvaluator {

  @Override
  public ConditionalContext evaluate(ConditionalContext context) {
    if (!ReflectionUtils.isAnnotationPresentFullCheck(context.getElementClass(), ConditionalOnWindows.class)) {
      return context;
    }

    boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
    context.setConditionalCheckPassed(isWindows);
    if (!isWindows) {
      context.getConditionalCheckResults().add(
          String.format("Current OS is not Windows, %s evaluation failed", ConditionalOnWindows.class.getSimpleName()));
      log.warn("Current OS is not Windows, {} evaluation failed", ConditionalOnWindows.class.getSimpleName());
    }
    return context;
  }
}
