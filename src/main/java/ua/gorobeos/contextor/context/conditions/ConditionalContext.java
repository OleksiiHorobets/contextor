package ua.gorobeos.contextor.context.conditions;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConditionalContext {
  private Class<?> elementClass;
  private boolean isCircuitBreaker;
  private boolean conditionalCheckPassed;
  List<String> conditionalCheckResults;
  Map<Class<? extends Annotation>, List<Annotation>> processedConditions;
}
