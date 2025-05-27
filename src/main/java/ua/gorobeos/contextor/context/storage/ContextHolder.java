package ua.gorobeos.contextor.context.storage;

import static ua.gorobeos.contextor.context.element.ElementDefinition.SINGLETON_SCOPE;

import ch.qos.logback.core.util.StringUtil;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ua.gorobeos.contextor.context.dependencies.DependencyResolver;
import ua.gorobeos.contextor.context.dependencies.SimpleDependencyResolver;
import ua.gorobeos.contextor.context.element.ElementDefinition;
import ua.gorobeos.contextor.context.element.MethodDefinedElementDefinition;
import ua.gorobeos.contextor.context.exceptions.ElementCreationException;
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

    classesFound.forEach(elementDefinitionReaderFacade::addElementDefinition);

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

    return createElementInstance(elementDefinition);
  }


  private Object createElementInstance(ElementDefinition elementDefinition) {
    var dependenciesDefinitions = elementDefinition.getDependencies()
        .stream()
        .map(dependencyResolver::retrieveDependency)
        .map(ElementDefinition::getName)
        .map(this::getElement)
        .toArray();

    log.debug("Creating instance of element: {} with dependencies: {}", elementDefinition.getName(), dependenciesDefinitions);

    Object createdElement;
    if (elementDefinition instanceof MethodDefinedElementDefinition methodDefined) {
      log.debug("Creating element using method defined element definition: {}", methodDefined);
      var configElement = getElement(StringUtil.lowercaseFirstLetter(methodDefined.getConfigClass().getSimpleName()));
      try {
        log.debug("------Invoking init method '{}' on config element: {}", methodDefined.getInitMethod().getName(), configElement);
        createdElement = methodDefined.getInitMethod().invoke(configElement, dependenciesDefinitions);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new ElementCreationException(
            "Failed to invoke init method '%s' on config element '%s' with dependencies: %s".formatted(
                methodDefined.getInitMethod().getName(), configElement, dependenciesDefinitions), e);
      }
    } else {
      createdElement = initElementByConstructor(elementDefinition, dependenciesDefinitions);
    }

    log.info("Element '{}' created successfully with dependencies: {}", elementDefinition.getName(), dependenciesDefinitions);
    if (SINGLETON_SCOPE.equals(elementDefinition.getScope())) {
      log.debug("Registering element '{}' in context holder", elementDefinition.getName());
      nameToElementMap.put(elementDefinition.getName(), createdElement);
      return createdElement;
    }
    return createdElement;
  }

  private static Object initElementByConstructor(ElementDefinition elementDefinition, Object[] dependenciesDefinitions) {
    var initConstructor = elementDefinition.getInitConstructor();
    initConstructor.setAccessible(true);
    try {
      return initConstructor.newInstance(dependenciesDefinitions);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new ElementCreationException(
          "Failed to create instance of element '%s' using constructor '%s' with dependencies: %s".formatted(
              elementDefinition.getName(), initConstructor, dependenciesDefinitions), e);
    }
  }


}
