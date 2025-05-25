package ua.gorobeos.contextor.context.scanner;


import static ua.gorobeos.contextor.context.utils.ScannerUtils.isAnnotationPresentFullCheck;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import ua.gorobeos.contextor.context.annotations.Element;
import ua.gorobeos.contextor.context.utils.ScannerUtils;

@UtilityClass
public class ClasspathElementScanner {

  public Set<Class<?>> scanClassesForElements(String packageToScan) {
    return ScannerUtils.scanClasses(packageToScan)
        .stream()
        .filter(clazz -> isAnnotationPresentFullCheck(clazz, Element.class))
        .collect(Collectors.toSet());
  }

}
