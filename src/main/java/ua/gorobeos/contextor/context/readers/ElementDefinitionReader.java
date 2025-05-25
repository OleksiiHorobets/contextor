package ua.gorobeos.contextor.context.readers;

import ua.gorobeos.contextor.context.element.ElementDefinition;

public interface ElementDefinitionReader {

  ElementDefinition readElementDefinition(Class<?> clazz);
}
