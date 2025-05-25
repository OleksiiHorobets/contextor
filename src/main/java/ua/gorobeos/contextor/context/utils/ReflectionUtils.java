package ua.gorobeos.contextor.context.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.gorobeos.contextor.context.annotations.Qualifier;
import ua.gorobeos.contextor.context.element.DependencyDefinition;

@UtilityClass
public class ReflectionUtils {

  private static final Logger log = LoggerFactory.getLogger(ReflectionUtils.class);

  public static boolean isAnnotationPresentFullCheck(Class<?> clazz, Class<? extends Annotation> targerAnnotation) {
    return getAnnotationsFromClass(clazz)
        .stream()
        .anyMatch(annotation -> annotation.equals(targerAnnotation));
  }

  public static Set<Class<? extends Annotation>> getAnnotationsFromClass(@Nonnull Class<?> targetClazz) {
    log.trace("Getting annotations from class: {}", targetClazz.getName());
    Set<Annotation> result = new HashSet<>();
    while (targetClazz != null && targetClazz != Object.class) {
      Collections.addAll(result, targetClazz.getDeclaredAnnotations());
      targetClazz = targetClazz.getSuperclass();
    }
    log.trace("Found {} annotations", result.size());
    return result.stream()
        .map(Annotation::annotationType)
        .collect(Collectors.toSet());
  }


  public static <T extends Annotation> Optional<T> getSingleAnnotationFromClass(Class<?> targetClazz, Class<T> targetAnnotation) {
    log.trace("Getting annotations from class: {} for annotation: {}", targetClazz.getName(), targetAnnotation.getName());
    return Optional.ofNullable(targetClazz.getAnnotation(targetAnnotation));
  }

  public static <T> Optional<T> getValueFromAnnotation(Class<?> targetClazz, Class<? extends Annotation> annotation, String fieldName,
      Class<T> type) {
    log.trace("Getting value from annotation: {} for field: {} in class: {}", annotation.getName(), fieldName, targetClazz.getName());
    return getSingleAnnotationFromClass(targetClazz, annotation)
        .map(an -> extractValue(an, fieldName))
        .map(val -> castToType(val, type));
  }

  public static Collection<DependencyDefinition> mapParametersToDependencyDefinitions(Parameter[] parameters) {
    if (parameters == null || parameters.length == 0) {
      return Collections.emptyList();
    }
    return Arrays.stream(parameters)
        .map(ReflectionUtils::mapParameterToDependencyDefinition)
        .toList();
  }

  private static DependencyDefinition mapParameterToDependencyDefinition(Parameter param) {
    Class<?> paramType = param.getType();
    String paramName = param.getName();
    String qualifier = Optional.ofNullable(param.getAnnotation(Qualifier.class))
        .map(Qualifier::value)
        .orElse(null);

    log.debug("Mapping parameter to DependencyDefinition: type={}, name={}, qualifier={}",
        paramType.getName(), paramName, qualifier);
    return DependencyDefinition.builder()
        .name(paramName)
        .qualifier(qualifier)
        .clazz(paramType)
        .build();
  }

  private static <T> T castToType(Object o, Class<T> targetType) {
    try {
      return targetType.cast(o);
    } catch (ClassCastException e) {
      log.error("Error casting value to type: {}", targetType.getName(), e);
      throw new IllegalArgumentException("Cannot cast value to type: " + targetType.getName(), e);
    }
  }

  private static Object extractValue(Annotation annotation, String fieldName) {
    try {
      return annotation.annotationType().getMethod(fieldName).invoke(annotation);
    } catch (Exception e) {
      log.error("Error getting value from annotation", e);
      throw new IllegalArgumentException("Error getting value from annotation", e);
    }
  }
}
