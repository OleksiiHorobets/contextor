package ua.gorobeos.contextor.context.storage;

import java.util.Collection;
import ua.gorobeos.contextor.context.element.ElementDefinition;

public interface ElementDefinitionHolder {

  void addElementDefinition(ElementDefinition elementDefinition);

  ElementDefinition getElementDefinition(String elementName);

  Collection<ElementDefinition> getElementDefinitions();

}
