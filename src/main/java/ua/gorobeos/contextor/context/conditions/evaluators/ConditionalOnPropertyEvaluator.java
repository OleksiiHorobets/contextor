package ua.gorobeos.contextor.context.conditions.evaluators;

import static ua.gorobeos.contextor.context.utils.ReflectionUtils.getValueFromAnnotation;
import static ua.gorobeos.contextor.context.utils.ReflectionUtils.isAnnotationPresentFullCheck;

import lombok.extern.slf4j.Slf4j;
import ua.gorobeos.contextor.context.annotations.conditions.ConditionalOnProperty;
import ua.gorobeos.contextor.context.conditions.ConditionalContext;
import ua.gorobeos.contextor.context.conditions.ConditionalEvaluator;
import ua.gorobeos.contextor.context.config.ConfigurationReader;

@Slf4j
public class ConditionalOnPropertyEvaluator implements ConditionalEvaluator {

  @Override
  public ConditionalContext evaluate(ConditionalContext context) {
    Class<?> element = context.getElementClass();
    if (element == null) {
      return context;
    }
    if (!isAnnotationPresentFullCheck(element, ConditionalOnProperty.class)) {
      return context;
    }

    var name = getValueFromAnnotation(element, ConditionalOnProperty.class, "name", String.class);
    var value = getValueFromAnnotation(element, ConditionalOnProperty.class, "value", String.class);
    if (name.isEmpty() || value.isEmpty()) {
      log.warn("ConditionalOnProperty annotation is missing 'name' or 'value' attribute in class: {}", element.getName());
      context.setConditionalCheckPassed(false);
      context.getConditionalCheckResults().add(
          String.format("ConditionalOnProperty annotation is missing 'name' or 'value' attribute in class: %s", element.getName()));
      return context;
    }
    var propertyValue = ConfigurationReader.getOrDefault(name.get(), null);
    if (propertyValue == null || !propertyValue.equals(value.get())) {
      log.warn("Property '{}' with value '{}' does not exist, {} evaluation failed", name, value,
          ConditionalOnProperty.class.getSimpleName());
      context.setConditionalCheckPassed(false);
      context.getConditionalCheckResults().add(
          String.format("Property '%s' with value '%s' does not exist, evaluation failed", name, value));
    }
    return context;
  }
}
