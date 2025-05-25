package ua.gorobeos.contextor.context.utils;

import java.util.Set;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

@UtilityClass
@Slf4j
public class ScannerUtils {

  public Set<Class<?>> scanPackage(String packageToScan) {
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

}
