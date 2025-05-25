package ua.gorobeos.contextor.context.readers;

import static ua.gorobeos.contextor.context.element.ElementDefinition.PROTOTYPE_SCOPE;
import static ua.gorobeos.contextor.context.element.ElementDefinition.SINGLETON_SCOPE;
import static ua.gorobeos.contextor.context.utils.ReflectionUtils.mapParametersToDependencyDefinitions;

import ch.qos.logback.core.util.StringUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ua.gorobeos.contextor.context.annotations.Injected;
import ua.gorobeos.contextor.context.element.DependencyDefinition;
import ua.gorobeos.contextor.context.element.ElementDefinition;
import ua.gorobeos.contextor.context.exceptions.ElementDefinitionCreationException;
import ua.gorobeos.contextor.context.exceptions.InvalidElementScopeException;
import ua.gorobeos.contextor.context.exceptions.MultipleConstructorCandidatesException;
import ua.gorobeos.contextor.context.exceptions.NoSuitableConstructorException;
import ua.gorobeos.contextor.context.storage.ElementDefinitionHolder;

@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public abstract class ElementDefinitionReader {

  protected static final Set<String> VALID_SCOPES = Set.of(SINGLETON_SCOPE, PROTOTYPE_SCOPE);

  ElementDefinitionHolder elementDefinitionHolder;

  public void readElementDefinition(Class<?> clazz) {
    if (clazz == null) {
      log.error("Cannot read element definition for null class");
      throw new ElementDefinitionCreationException("Class cannot be null");
    }
    log.debug("Reading element definition for class: {}", clazz.getName());
    String elementName = resolveElementName(clazz);
    log.debug("Resolved element name: {}", elementName);

    String elementScope = resolveScope(clazz);
    log.debug("Resolved element scope: {}", elementScope);

    Constructor<?> initConstructor = resolveElementInitConstructor(clazz);
    log.debug("Resolved init constructor: {}", initConstructor);

    Collection<DependencyDefinition> dependencyDefinitions = resolveDependencies(initConstructor);

    ElementDefinition elementDefinition = getElementDefinitionContainer();
    elementDefinition.setName(elementName);
    elementDefinition.setType(clazz);
    elementDefinition.setInitConstructor(initConstructor);
    elementDefinition.setDependencies(dependencyDefinitions);
  }

  public Collection<DependencyDefinition> resolveDependencies(Executable method) {
    if (method == null) {
      log.error("No suitable constructor found for element definition");
      throw new NoSuitableConstructorException("No suitable constructor found for element definition");
    }
    log.debug("Resolving dependencies for method: {}", method);
    var dependencyDefinitions = mapParametersToDependencyDefinitions(method.getParameters());
    log.debug("Resolved dependencies: {}", dependencyDefinitions);
    return dependencyDefinitions;
  }

  public Constructor<?> resolveElementInitConstructor(Class<?> clazz) {
    var constructors = clazz.getDeclaredConstructors();
    if (constructors.length == 0) {
      log.error("No suitable constructors found for class: {}", clazz.getName());
      throw new NoSuitableConstructorException(
          "No suitable constructors found for class: %s".formatted(clazz.getName()));
    }
    if (constructors.length == 1) {
      var initConstructor = constructors[0];
      log.trace("Single constructor [{}] found for class: {}", initConstructor, clazz.getName());
      return initConstructor;
    }

    var injectedConstructors = Arrays.stream(constructors)
        .filter(constructor -> constructor.isAnnotationPresent(Injected.class))
        .toList();

    if (injectedConstructors.size() == 1) {
      log.trace("Single @Injected constructor [{}] found for class: {}", injectedConstructors.get(0), clazz.getName());
      return injectedConstructors.get(0);
    }

    if (injectedConstructors.size() > 1) {
      log.error("Multiple @Injected constructors found for class: {}", clazz.getName());
      throw new MultipleConstructorCandidatesException(
          "Multiple @Injected constructors found for class: %s".formatted(clazz.getName()));
    }

    log.error("Could not resolve multiple non-annotated constructors for class: {}", clazz.getName());
    throw new MultipleConstructorCandidatesException(
        "Could not resolve multiple non-annotated constructors for class: %s".formatted(clazz.getName()));
  }

  protected abstract String resolveScope(Class<?> clazz);

  protected abstract String resolveElementName(Class<?> clazz);

  protected void checkIfValidScopeProvided(String scope) {
    if (!VALID_SCOPES.contains(scope)) {
      throw new InvalidElementScopeException(
          "Invalid scope name provided: %s. Valid scopes are: %s".formatted(scope, VALID_SCOPES));
    }
  }

  protected String getDefaultElementName(Class<?> clazz) {
    if (clazz == null) {
      log.error("Cannot resolve element name for null class");
      throw new IllegalArgumentException("Class cannot be null");
    }

    var className = clazz.getSimpleName();
    var elementName = StringUtil.lowercaseFirstLetter(className);
    log.debug("Default element name resolved: {}", elementName);
    return elementName;
  }

  protected abstract ElementDefinition getElementDefinitionContainer();
}

