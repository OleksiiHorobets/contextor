package ua.gorobeos.contextor.context.utils;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

@UtilityClass
@Slf4j
public class ScannerUtils {

  public Set<Class<?>> scanClasses(String packageToScan) {
    log.info("Scanning classes in package: {}", packageToScan);
    validatePackageBasePath(packageToScan);
    var foundClasses = new Reflections(packageToScan, Scanners.SubTypes.filterResultsBy(c -> true))
        .getSubTypesOf(Object.class);

    log.trace("Found {} classes in package: {}", foundClasses.size(), packageToScan);
    return foundClasses;
  }


  private void validatePackageBasePath(String packagePath) {
    if (packagePath == null || packagePath.isEmpty()) {
      String errorMessage = "Package path cannot be null or empty.";
      log.error(errorMessage);
      throw new IllegalArgumentException(errorMessage);
    }
    if (!packagePath.matches("^[a-zA-Z_][a-zA-Z0-9_.]*$")) {
      String errorMessage = "Invalid package name: " + packagePath;
      log.error(errorMessage);
      throw new IllegalArgumentException(errorMessage);
    }
  }


  public static boolean isAnnotationPresentFullCheck(Class<?> clazz, Class<? extends Annotation> targerAnnotation) {
    return getAnnotationsFromClass(clazz)
        .stream()
        .anyMatch(annotation -> annotation.equals(targerAnnotation));
  }

  public static Set<Class<? extends Annotation>> getAnnotationsFromClass(Class<?> targetClazz) {
    Set<Annotation> result = new HashSet<>();
    while (targetClazz != null && targetClazz != Object.class) {
      Collections.addAll(result, targetClazz.getDeclaredAnnotations());
      targetClazz = targetClazz.getSuperclass();
    }

    return result.stream()
        .map(Annotation::annotationType)
        .collect(Collectors.toSet());
  }
}
