package ua.gorobeos.contextor.context.dependencies;

import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ua.gorobeos.contextor.context.element.ElementDefinition;
import ua.gorobeos.contextor.context.exceptions.UnresolvableDependencyException;
import ua.gorobeos.contextor.context.storage.ElementDefinitionHolder;

@Slf4j
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class SimpleDependencyResolver implements DependencyResolver {

  ElementDefinitionHolder elementDefinitionHolder;

  @Override
  public ElementDefinition getDependencyDefinitionForClass(Class<?> dependencyClass) {
    log.trace("Retrieving dependency definition for class: {}", dependencyClass.getName());
    Collection<ElementDefinition> compatibleElements = elementDefinitionHolder.getElementDefinitionsByType(dependencyClass);

    if (compatibleElements.isEmpty()) {
      log.error("No element definition found for class: {}", dependencyClass.getName());
      throw new UnresolvableDependencyException(
          "No element definition found for class '%s'".formatted(dependencyClass.getName()));
    }
    if (compatibleElements.size() == 1) {
      log.debug("Single compatible element found for class: {}", dependencyClass.getName());
      return compatibleElements.iterator().next();
    }

    var primaryElements = compatibleElements.stream()
        .filter(ElementDefinition::getIsPrimary)
        .toList();

    if (primaryElements.size() > 1) {
      log.error("Multiple @Primary elements found for class: {}", dependencyClass.getName());
      throw new UnresolvableDependencyException(
          "Multiple primary elements found for class '%s'".formatted(dependencyClass.getName()));
    }
    if (primaryElements.size() == 1) {
      log.debug("Primary element found for class: {}", dependencyClass.getName());
      return primaryElements.get(0);
    }

    log.error("Multiple compatible elements found for class: {}", dependencyClass.getName());
    throw new UnresolvableDependencyException(
        "Multiple compatible elements found for class '%s'. Please specify a primary element.".formatted(dependencyClass.getName()));
  }

  @Override
  public ElementDefinition retrieveDependency(DependencyDefinition definition) {
    log.trace("Retrieving dependency definition: {}", definition);
    ElementDefinition elementDefinition = Optional.ofNullable(definition.getQualifier())
        .map(elementDefinitionHolder::getElementDefinition)
        .orElseGet(() -> elementDefinitionHolder.getElementDefinition(definition.getName()))
        .orElseGet(() -> getDependencyDefinitionForClass(definition.getClazz()));

    if (!definition.getClazz().isAssignableFrom(elementDefinition.getType())) {
      log.error("Dependency '%s' cannot be resolved. Element definition '%s' is not assignable from '%s'.",
          definition.getName(), elementDefinition.getType().getName(), definition.getClazz().getName());
      throw new UnresolvableDependencyException(
          "Dependency '%s' cannot be resolved. Element definition '%s' is not assignable from '%s'.".formatted(
              definition.getName(), elementDefinition.getType().getName(), definition.getClazz().getName()));
    }
    log.trace("Dependency definition retrieved: {}", elementDefinition);
    return elementDefinition;
  }


}
