package ua.gorobeos.contextor.context.conditions.evaluators;

import static ua.gorobeos.contextor.context.utils.ReflectionUtils.getValueFromAnnotation;
import static ua.gorobeos.contextor.context.utils.ReflectionUtils.isAnnotationPresentFullCheck;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import ua.gorobeos.contextor.context.annotations.conditions.ConditionalOnFilePresence;
import ua.gorobeos.contextor.context.conditions.ConditionalContext;
import ua.gorobeos.contextor.context.conditions.ConditionalEvaluator;

@Slf4j
public class ConditionalOnFilePresenceEvaluator implements ConditionalEvaluator {

  @Override
  public ConditionalContext evaluate(ConditionalContext context) {
    // Беремо клас елемента з контексту
    Class<?> element = context.getElementClass();
    if (element == null) {
      return context;
    }
    // Перевіряємо, чи є у класу анотація, яка нас цікавить ConditionalOnFilePresence
    if (!isAnnotationPresentFullCheck(element, ConditionalOnFilePresence.class)) {
      return context;
    }
    // Отримуємо масив шляхів до файлів наявність яких треба перевірити
    var filePaths = getValueFromAnnotation(element, ConditionalOnFilePresence.class, "filePaths", String[].class)
        .orElseGet(() -> new String[0]);

    // Перевіряємо кожен шлях до файлу
    for (String filePath : filePaths) {
      var res = getClass().getClassLoader().getResource(filePath);
      if (res == null && !Files.exists(Path.of(filePath))) {
        log.warn("File {} does not exist, {} evaluation failed", filePath, ConditionalOnFilePresence.class.getSimpleName());
        context.setConditionalCheckPassed(false);
        context.getConditionalCheckResults().add(
            String.format("File '%s' does not exist, evaluation failed", filePath));
        return context;
      }
    }
    return context;
  }
}
