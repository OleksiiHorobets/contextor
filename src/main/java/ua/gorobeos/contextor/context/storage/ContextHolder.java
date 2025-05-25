package ua.gorobeos.contextor.context.storage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ua.gorobeos.contextor.context.element.ElementDefinition;
import ua.gorobeos.contextor.context.scanner.ClasspathElementScanner;

@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ContextHolder {

  ConcurrentMap<Class<?>, ElementDefinition> elementDefinitionMap = new ConcurrentHashMap<>();

  public void initializeContext(Class<?> initClazz) {
    var classesFound = ClasspathElementScanner.scanPackages(initClazz);
    System.out.println(classesFound);
  }


}
