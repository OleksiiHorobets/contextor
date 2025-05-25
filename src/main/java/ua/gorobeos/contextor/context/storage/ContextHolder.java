package ua.gorobeos.contextor.context.storage;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ua.gorobeos.contextor.context.readers.ElementDefinitionReaderFacade;
import ua.gorobeos.contextor.context.readers.ElementDefinitionReaderFacadeImpl;
import ua.gorobeos.contextor.context.scanner.ClasspathElementScanner;

@Slf4j
@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ContextHolder {

  ElementDefinitionHolder elementDefinitionHolder;
  ElementDefinitionReaderFacade elementDefinitionReaderFacade;

  public static ContextHolder initializeContext(Class<?> initClazz) {
    log.info("Initializing context with class: {}", initClazz.getName());
    var classesFound = ClasspathElementScanner.scanPackages(initClazz);
    log.debug("Classes found during scanning: {}", classesFound);
    log.info("Registering class definitions in context holder");

    ElementDefinitionHolder elementDefinitionHolder = new DefaultElementDefinitionHolder();
    ElementDefinitionReaderFacade elementDefinitionReaderFacade = new ElementDefinitionReaderFacadeImpl(elementDefinitionHolder);

    classesFound.stream()
        .forEach(elementDefinitionReaderFacade::addElementDefinition);
    log.info("Context initialized with {} element definitions", elementDefinitionHolder.getElementDefinitions().size());

    return new ContextHolder(elementDefinitionHolder, elementDefinitionReaderFacade);
  }

}
