package ua.gorobeos.contextor.context.dependencies.checker;

import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ua.gorobeos.contextor.context.dependencies.DependencyResolver;
import ua.gorobeos.contextor.context.element.ElementDefinition;
import ua.gorobeos.contextor.context.exceptions.CircularDependencyException;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class DfsDependencyCircularChecker implements DependencyCircularChecker {

  DependencyResolver dependencyResolver;

  Set<String> visitedElements = new HashSet<>();

  @Override
  public void checkForCircularDependencies(ElementDefinition current) {
    log.debug("Checking for circular dependencies for element: {}", current.getName());
    checkForCircularDependencies(current, null);
  }


  private void checkForCircularDependencies(ElementDefinition current, ElementDefinition previous) {
    if (visitedElements.contains(current.getName())) {
      log.error("Circular dependency detected: {} -> {}", previous.getName(), current.getName());
      throw new CircularDependencyException(
          String.format("Circular dependency detected between '%s'  -> '%s'", previous.getName(), current.getName()));
    }
    visitedElements.add(current.getName());
    current.getDependencies()
        .stream()
        .map(dependencyResolver::retrieveDependency)
        .forEach(element -> checkForCircularDependencies(element, current));

    visitedElements.clear();
  }
}
