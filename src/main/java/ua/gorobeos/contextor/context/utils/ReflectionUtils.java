package ua.gorobeos.contextor.context.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
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
import ua.gorobeos.contextor.context.dependencies.DependencyDefinition;

@UtilityClass
public class ReflectionUtils {

  private static final Logger log = LoggerFactory.getLogger(ReflectionUtils.class);

  public static boolean isAnnotationPresentFullCheck(Class<?> clazz, Class<? extends Annotation> targerAnnotation) {
    return getAnnotationsFromClassFullCheck(clazz)
        .stream()
        .anyMatch(annotation -> annotation.equals(targerAnnotation));
  }

  public static Set<Class<? extends Annotation>> getAnnotationsFromClassFullCheck(@Nonnull Class<?> targetClazz) {
    log.trace("Getting annotations from class: {}", targetClazz.getName());
    return org.reflections.ReflectionUtils.getAllAnnotations(targetClazz)
        .stream()
        .map(Annotation::annotationType)
        .collect(Collectors.toSet());
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

  public static Collection<Method> getMethodsAnnotatedBy(Class<?> clazz, Class<? extends Annotation> annotation) {
    if (clazz == null) {
      log.error("Class cannot be null");
      throw new IllegalArgumentException("Class cannot be null");
    }
    if (annotation == null) {
      log.error("Annotation cannot be null");
      throw new IllegalArgumentException("Annotation cannot be null");
    }

    log.trace("Getting methods from class: {}", clazz.getName());
    return Arrays.stream(clazz.getDeclaredMethods())
        .filter(method -> !method.isSynthetic() && !method.isBridge())
        .filter(method -> method.isAnnotationPresent(annotation))
        .toList();
  }

  public static <T> Optional<T> getValueFromAnnotationOnMethod(Method method, Class<? extends Annotation> annotation, String fieldName,
      Class<T> type) {
    log.trace("Getting value from annotation: {} for field: {} in method: {}", annotation.getName(), fieldName, method.getName());
    return getSingleAnnotationFromMethod(method, annotation)
        .map(an -> extractValue(an, fieldName))
        .map(val -> castToType(val, type));
  }

  public static <T extends Annotation> Optional<T> getSingleAnnotationFromMethod(Method method, Class<T> targetAnnotation) {
    log.trace("Getting annotation: {} from method: {}", targetAnnotation.getName(), method.getName());
    return Optional.ofNullable(method.getAnnotation(targetAnnotation));
  }

}
