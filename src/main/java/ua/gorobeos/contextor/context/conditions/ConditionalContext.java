package ua.gorobeos.contextor.context.conditions;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConditionalContext {
  // Клас, який підлягає умовній перевірці
  Class<?> elementClass;
  // Прапорець, який вказує, чи увімкнено "переривання ланцюга"
  boolean isCircuitBreaker;
  // Прапорець, який вказує, чи пройшла умовна перевірка
  boolean conditionalCheckPassed;
  // Список результатів умовних перевірок
  List<String> conditionalCheckResults;
}
