package ua.gorobeos.contextor.context.conditions;

import java.util.ArrayList;
import java.util.List;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ua.gorobeos.contextor.context.conditions.evaluators.ConditionalOnFilePresenceEvaluator;
import ua.gorobeos.contextor.context.conditions.evaluators.ConditionalOnLinuxEvaluator;
import ua.gorobeos.contextor.context.conditions.evaluators.ConditionalOnPropertyEvaluator;
import ua.gorobeos.contextor.context.conditions.evaluators.ConditionalOnWebRequestEvaluator;
import ua.gorobeos.contextor.context.conditions.evaluators.ConditionalOnWindowsEvaluator;
import ua.gorobeos.contextor.context.config.ConfigurationReader;

@Slf4j
@UtilityClass
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ConditionEvaluationUtils {

  private static final List<ConditionalEvaluator> conditionalEvaluator = new ArrayList<>(List.of(
      new ConditionalOnFilePresenceEvaluator(),
      new ConditionalOnPropertyEvaluator(),
      new ConditionalOnLinuxEvaluator(),
      new ConditionalOnWindowsEvaluator(),
      new ConditionalOnWebRequestEvaluator()
  ));


  public static ConditionalContext evaluate(Class<?> elementType) {
    var context = initializeConditionalContext(elementType);

    for (ConditionalEvaluator evaluator : conditionalEvaluator) {
      context = evaluator.evaluate(context);
      if (!context.isConditionalCheckPassed() && context.isCircuitBreaker()) {
        log.warn("Circuit breaker is on and evalutaion failed. Skipping rest {} evaluations", elementType.getSimpleName());
        return context;
      }
    }

    return context;
  }

  private static ConditionalContext initializeConditionalContext(Class<?> elementClass) {
    return ConditionalContext.builder()
        .elementClass(elementClass)
        .isCircuitBreaker(ConfigurationReader.getOrDefault("conditional.checks.circuit.breaker", false, Boolean::parseBoolean))
        .conditionalCheckResults(new ArrayList<>())
        .conditionalCheckPassed(true)
        .build();
  }
}
