package ua.gorobeos.contextor.context.readers;

import static ua.gorobeos.contextor.context.element.ElementDefinition.PROTOTYPE_SCOPE;
import static ua.gorobeos.contextor.context.element.ElementDefinition.SINGLETON_SCOPE;

import ch.qos.logback.core.util.StringUtil;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ua.gorobeos.contextor.context.element.ElementDefinition;
import ua.gorobeos.contextor.context.exceptions.ElementDefinitionCreationException;
import ua.gorobeos.contextor.context.exceptions.InvalidElementScopeException;
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

    ElementDefinition elementDefinition = getElementDefinitionContainer();
    elementDefinition.setName(elementName);
    elementDefinition.setType(clazz);
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

