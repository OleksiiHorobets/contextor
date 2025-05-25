package ua.gorobeos.contextor.context.readers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ua.gorobeos.contextor.context.storage.ElementDefinitionHolder;

@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ElementDefinitionReader {

  ElementDefinitionHolder elementDefinitionHolder;


  public void readElementDefinition(Class<?> clazz) {
    log.debug("Reading element definition for class: {}", clazz.getName());
    
  }
}
