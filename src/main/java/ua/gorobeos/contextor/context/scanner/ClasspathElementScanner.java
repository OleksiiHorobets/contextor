package ua.gorobeos.contextor.context.scanner;


import static ua.gorobeos.contextor.context.utils.ReflectionUtils.isAnnotationPresentFullCheck;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ua.gorobeos.contextor.context.annotations.Element;
import ua.gorobeos.contextor.context.annotations.ElementScan;
import ua.gorobeos.contextor.context.exceptions.ContainerInitException;
import ua.gorobeos.contextor.context.utils.ReflectionUtils;
import ua.gorobeos.contextor.context.utils.ScannerUtils;

@Slf4j
@UtilityClass
public class ClasspathElementScanner {


  public Collection<Class<?>> scanPackages(Class<?> initClazz) {
    log.info("Initializing context... Init class: {}", initClazz.getSimpleName());
    List<String> resolvedPackage = resolvePackages(initClazz);
    log.debug("Resolved packages: {}", resolvedPackage);

    var classesFound = resolvedPackage.stream()
        .map(ClasspathElementScanner::scanClassesForElements)
        .flatMap(Set::stream)
        .collect(Collectors.toSet());

    log.debug("Classes found in packages: {}", classesFound);

    return classesFound;
  }

  private Set<Class<?>> scanClassesForElements(String packageToScan) {
    return ScannerUtils.scanPackage(packageToScan)
        .stream()
        .filter(clazz -> isAnnotationPresentFullCheck(clazz, Element.class))
        .collect(Collectors.toSet());
  }

  private List<String> resolvePackages(Class<?> initClazz) {
    log.debug("Resolving packages for class: {}", initClazz.getName());
    var elementScanAnnotation = ReflectionUtils.getValueFromAnnotation(initClazz, ElementScan.class, "basePackages", String[].class)
        .orElseThrow(() -> new ContainerInitException("ElementScan annotation not found on class: " + initClazz.getName()));
    if (elementScanAnnotation.length == 0) {
      log.error("No base packages specified: {}", initClazz.getName());
      throw new ContainerInitException("No base packages specified in ElementScan annotation on class: " + initClazz.getName());
    }

    return Arrays.stream(elementScanAnnotation).toList();
  }

}
