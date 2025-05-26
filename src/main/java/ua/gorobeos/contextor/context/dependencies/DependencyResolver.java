package ua.gorobeos.contextor.context.dependencies;

import ua.gorobeos.contextor.context.element.ElementDefinition;

public interface DependencyResolver {

  ElementDefinition getDependencyDefinitionForClass(Class<?> dependencyClass);

  ElementDefinition retrieveDependency(DependencyDefinition dependencyDefinition);

}
