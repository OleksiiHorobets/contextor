package ua.gorobeos.contextor.context.storage;

import ua.gorobeos.contextor.context.element.ElementDefinition;

public interface ElementDefinitionHolder {

  void addElementDefinition(String elementName, ElementDefinition elementDefinition);

  ElementDefinition getElementDefinition(String elementName);
}
