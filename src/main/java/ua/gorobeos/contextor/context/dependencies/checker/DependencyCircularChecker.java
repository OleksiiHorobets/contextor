package ua.gorobeos.contextor.context.dependencies.checker;

import ua.gorobeos.contextor.context.element.ElementDefinition;

public interface DependencyCircularChecker {

  void checkForCircularDependencies(ElementDefinition elementDefinition);

}
