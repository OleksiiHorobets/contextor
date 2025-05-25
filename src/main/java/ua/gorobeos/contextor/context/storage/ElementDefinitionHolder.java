package ua.gorobeos.contextor.context.storage;

import java.util.Collection;
import java.util.Optional;
import ua.gorobeos.contextor.context.element.ElementDefinition;

public interface ElementDefinitionHolder {

  void addElementDefinition(ElementDefinition elementDefinition);

  Optional<ElementDefinition> getElementDefinition(String elementName);

  Collection<ElementDefinition> getElementDefinitions();

  Collection<ElementDefinition> getElementDefinitionsByType(Class<?> type);

}
