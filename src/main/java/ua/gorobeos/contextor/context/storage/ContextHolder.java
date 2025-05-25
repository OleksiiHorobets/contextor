package ua.gorobeos.contextor.context.storage;

import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ua.gorobeos.contextor.context.element.DependencyDefinition;
import ua.gorobeos.contextor.context.element.ElementDefinition;
import ua.gorobeos.contextor.context.exceptions.UnresolvableDependencyException;
import ua.gorobeos.contextor.context.readers.ElementDefinitionReaderFacade;
import ua.gorobeos.contextor.context.readers.ElementDefinitionReaderFacadeImpl;
import ua.gorobeos.contextor.context.scanner.ClasspathElementScanner;

@Slf4j
@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ContextHolder {

  Map<String, Object> nameToElementMap = new ConcurrentHashMap<>();
  ElementDefinitionHolder elementDefinitionHolder;
  ElementDefinitionReaderFacade elementDefinitionReaderFacade;

  public static ContextHolder initializeContext(Class<?> initClazz) {
    log.info("Initializing context with class: {}", initClazz.getName());
    var classesFound = ClasspathElementScanner.scanPackages(initClazz);
    log.debug("Classes found during scanning: {}", classesFound);
    log.info("Registering class definitions in context holder");

    ElementDefinitionHolder elementDefinitionHolder = new DefaultElementDefinitionHolder();
    ElementDefinitionReaderFacade elementDefinitionReaderFacade = new ElementDefinitionReaderFacadeImpl(elementDefinitionHolder);

    classesFound.stream()
        .forEach(elementDefinitionReaderFacade::addElementDefinition);
    log.info("Context initialized with {} element definitions", elementDefinitionHolder.getElementDefinitions().size());

    ContextHolder contextHolder = new ContextHolder(elementDefinitionHolder, elementDefinitionReaderFacade);

    elementDefinitionHolder.getElementDefinitions()
        .stream()
        .map(ElementDefinition::getName)
        .forEach(contextHolder::getElement);

    return contextHolder;
  }


  public Object getElement(String name) {
    if (nameToElementMap.containsKey(name)) {
      return nameToElementMap.get(name);
    }
    ElementDefinition elementDefinition = elementDefinitionHolder.getElementDefinition(name)
        .orElseThrow(() -> {
          log.error("Element definition for name '{}' not found", name);
          return new NoSuchElementException("Element definition for name '%s' not found".formatted(name));
        });

    log.debug("Creating new instance for element definition: {}", elementDefinition);

    Object elementInstance = createElementInstance(elementDefinition);

    return elementInstance;
  }

  @SneakyThrows
  private Object createElementInstance(ElementDefinition elementDefinition) {
    var dependenciesDefinitions = elementDefinition.getDependencies()
        .stream()
        .map(this::retrieveDependency)
        .map(ElementDefinition::getName)
        .map(this::getElement)
        .toArray();

    log.debug("Creating instance of element: {} with dependencies: {}", elementDefinition.getName(), dependenciesDefinitions);

    var initConstructor = elementDefinition.getInitConstructor();
    initConstructor.setAccessible(true);
    var createdElement = initConstructor.newInstance(dependenciesDefinitions);

    log.info("Element '{}' created successfully with dependencies: {}", elementDefinition.getName(), dependenciesDefinitions);
    if (!elementDefinition.getIsPrimary()) {
      return nameToElementMap.put(elementDefinition.getName(), createdElement);
    }
    return createdElement;
  }


  private ElementDefinition retrieveDependency(DependencyDefinition definition) {
    log.trace("Retrieving dependency definition: {}", definition);
    ElementDefinition elementDefinition = Optional.ofNullable(definition.getQualifier())
        .map(elementDefinitionHolder::getElementDefinition)
        .orElseGet(() -> elementDefinitionHolder.getElementDefinition(definition.getName()))
        .orElseGet(() -> getDependencyDefinitionForClass(definition.getClazz()));

    if (!definition.getClazz().isAssignableFrom(definition.getClazz())) {
      log.error("Dependency '%s' cannot be resolved. Element definition '%s' is not assignable to '%s'.",
          definition.getName(), elementDefinition.getName(), definition.getClazz().getName());
      throw new UnresolvableDependencyException(
          "Dependency '%s' cannot be resolved. Element definition '%s' is not assignable to '%s'.".formatted(
              definition.getName(), elementDefinition.getName(), definition.getClazz().getName()));
    }
    log.trace("Dependency definition retrieved: {}", elementDefinition);
    return elementDefinition;
  }


  private ElementDefinition getDependencyDefinitionForClass(Class<?> dependencyClass) {
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
      log.error("Multiple primary elements found for class: {}", dependencyClass.getName());
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
}
