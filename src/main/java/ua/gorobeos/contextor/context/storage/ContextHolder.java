package ua.gorobeos.contextor.context.storage;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ua.gorobeos.contextor.context.dependencies.DependencyResolver;
import ua.gorobeos.contextor.context.dependencies.SimpleDependencyResolver;
import ua.gorobeos.contextor.context.element.ElementDefinition;
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
  DependencyResolver dependencyResolver;

  public static ContextHolder initializeContext(Class<?> initClazz) {
    log.info("Initializing context with class: {}", initClazz.getName());
    var classesFound = ClasspathElementScanner.scanPackages(initClazz);
    log.debug("Classes found during scanning: {}", classesFound);
    log.info("Registering class definitions in context holder");

    ElementDefinitionHolder elementDefinitionHolder = new DefaultElementDefinitionHolder();
    ElementDefinitionReaderFacade elementDefinitionReaderFacade = new ElementDefinitionReaderFacadeImpl(elementDefinitionHolder);
    DependencyResolver dependencyResolver = new SimpleDependencyResolver(elementDefinitionHolder);

    classesFound.stream()
        .forEach(elementDefinitionReaderFacade::addElementDefinition);
    log.info("Context initialized with {} element definitions", elementDefinitionHolder.getElementDefinitions().size());

    ContextHolder contextHolder = new ContextHolder(elementDefinitionHolder, elementDefinitionReaderFacade, dependencyResolver);

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
        .map(dependencyResolver::retrieveDependency)
        .map(ElementDefinition::getName)
        .map(this::getElement)
        .toArray();

    log.debug("Creating instance of element: {} with dependencies: {}", elementDefinition.getName(), dependenciesDefinitions);

    var initConstructor = elementDefinition.getInitConstructor();
    initConstructor.setAccessible(true);
    var createdElement = initConstructor.newInstance(dependenciesDefinitions);

    log.info("Element '{}' created successfully with dependencies: {}", elementDefinition.getName(), dependenciesDefinitions);
    if (Boolean.FALSE.equals(elementDefinition.getIsPrimary())) {
      return nameToElementMap.put(elementDefinition.getName(), createdElement);
    }
    return createdElement;
  }


}
