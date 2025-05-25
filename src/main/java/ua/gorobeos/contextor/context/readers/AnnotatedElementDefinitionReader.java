package ua.gorobeos.contextor.context.readers;

import lombok.extern.slf4j.Slf4j;
import ua.gorobeos.contextor.context.annotations.Scope;
import ua.gorobeos.contextor.context.element.AnnotationElementDefinition;
import ua.gorobeos.contextor.context.element.ElementDefinition;
import ua.gorobeos.contextor.context.storage.ElementDefinitionHolder;
import ua.gorobeos.contextor.context.utils.ReflectionUtils;

@Slf4j
public class AnnotatedElementDefinitionReader extends ElementDefinitionReader {

  public AnnotatedElementDefinitionReader(ElementDefinitionHolder elementDefinitionHolder) {
    super(elementDefinitionHolder);
  }

  @Override
  public String resolveScope(Class<?> clazz) {
    var scope = ReflectionUtils.getValueFromAnnotation(clazz, Scope.class, "value", String.class)
        .orElseGet(() -> {
          log.debug("No specific scope provided for class: {}. Using default: {}", clazz, ElementDefinition.SINGLETON_SCOPE);
          return ElementDefinition.SINGLETON_SCOPE;
        });
    checkIfValidScopeProvided(scope);
    return scope;
  }

  @Override
  public String resolveElementName(Class<?> clazz) {
    log.debug("Resolving element name for class: {}", clazz.getName());
    return resolveNameFromAnnotations(clazz);
  }

  @Override
  public ElementDefinition getElementDefinitionContainer() {
    return new AnnotationElementDefinition();
  }

  private String resolveNameFromAnnotations(Class<?> clazz) {
    return ReflectionUtils.getValueFromAnnotation(clazz, ua.gorobeos.contextor.context.annotations.Element.class, "value", String.class)
        .orElseGet(() -> {
          log.debug("No specific name in @Element annotation found on class: {}", clazz.getName());
          return getDefaultElementName(clazz);
        });
  }

}
