package ua.gorobeos.contextor.context.readers;

import java.util.Collection;
import ua.gorobeos.contextor.context.element.ElementDefinition;

public interface ElementDefinitionReaderFacade {

  void addElementDefinition(Class<?> clazz);

  Collection<ElementDefinition> getElementDefinitions();
}
