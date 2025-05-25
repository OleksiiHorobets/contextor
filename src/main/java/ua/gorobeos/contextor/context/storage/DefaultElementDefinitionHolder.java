package ua.gorobeos.contextor.context.storage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ua.gorobeos.contextor.context.element.ElementDefinition;
import ua.gorobeos.contextor.context.exceptions.ElementNameConflictException;

@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DefaultElementDefinitionHolder implements ElementDefinitionHolder {

  ConcurrentMap<String, ElementDefinition> elementDefinitionMap = new ConcurrentHashMap<>();


  @Override
  public void addElementDefinition(String elementName, ElementDefinition elementDefinition) {
    if (elementDefinitionMap.containsKey(elementName)) {
      log.error("Conflict detected: Element definition for '{}' already exists. [{}]", elementName, elementDefinitionMap.get(elementName));
      throw new ElementNameConflictException("Element definition with name '%s' already exists".formatted(elementName));
    }
    elementDefinitionMap.put(elementName, elementDefinition);
    log.info("Added element definition for: {}", elementName);
  }

  @Override
  public ElementDefinition getElementDefinition(String elementName) {
    if (!elementDefinitionMap.containsKey(elementName)) {
      log.error("Element definition for '{}' not found.", elementName);
      return null;
    }
    return elementDefinitionMap.get(elementName);
  }


  public ConcurrentMap<String, ElementDefinition> getElementDefinitionMap() {
    return new ConcurrentHashMap<>(elementDefinitionMap);
  }
}