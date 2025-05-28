package ua.gorobeos.contextor.context.conditions.evaluators;

import lombok.extern.slf4j.Slf4j;
import ua.gorobeos.contextor.context.annotations.conditions.ConditionalOnLinux;
import ua.gorobeos.contextor.context.conditions.ConditionalContext;
import ua.gorobeos.contextor.context.conditions.ConditionalEvaluator;
import ua.gorobeos.contextor.context.utils.ReflectionUtils;

@Slf4j
public class ConditionalOnLinuxEvaluator implements ConditionalEvaluator {

  @Override
  public ConditionalContext evaluate(ConditionalContext context) {

    Class<?> element = context.getElementClass();
    if (ReflectionUtils.isAnnotationPresentFullCheck(element, ConditionalOnLinux.class)) {
      log.debug("Evaluating {} for element {}", ConditionalOnLinuxEvaluator.class.getSimpleName(), element.getName());
    } else {
      log.debug("{} is not present on element {}, skipping evaluation", ConditionalOnLinux.class.getSimpleName(), element.getName());
      return context;
    }

    String osName = System.getProperty("os.name").toLowerCase();
    if (!osName.contains("linux")) {
      log.warn("Current OS is not Linux, {} evaluation failed", ConditionalOnLinuxEvaluator.class.getSimpleName());
      context.setConditionalCheckPassed(false);
      context.getConditionalCheckResults().add(
          String.format("Current OS '%s' is not Linux, evaluation failed", osName));
    }
    return context;
  }
}
