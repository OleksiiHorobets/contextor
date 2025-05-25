package ua.gorobeos.contextor.context.readers;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ua.gorobeos.contextor.context.annotations.Primary;
import ua.gorobeos.contextor.context.annotations.Scope;
import ua.gorobeos.contextor.context.element.AnnotationElementDefinition;
import ua.gorobeos.contextor.context.element.ElementDefinition;
import ua.gorobeos.contextor.context.utils.ReflectionUtils;

@Slf4j
public class AnnotatedBaseElementDefinitionReader extends BaseElementDefinitionReader {


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
  public ElementDefinition readElementDefinition(Class<?> clazz) {
    var elementDefinition = super.readElementDefinition(clazz);
    elementDefinition.setIsPrimary(resolveIfIsPrimary(clazz));
    log.debug("Element definition created: {}", elementDefinition);
    return elementDefinition;
  }

  @Override
  public ElementDefinition getElementDefinitionContainerWithSpecifics() {
    return new AnnotationElementDefinition();
  }

  private String resolveNameFromAnnotations(Class<?> clazz) {
    return ReflectionUtils.getValueFromAnnotation(clazz, ua.gorobeos.contextor.context.annotations.Element.class, "value", String.class)
        .filter(StringUtils::isNotBlank)
        .orElseGet(() -> {
          log.debug("No specific name in @Element annotation found on class: {}", clazz.getName());
          return getDefaultElementName(clazz);
        });
  }

  private boolean resolveIfIsPrimary(Class<?> clazz) {
    return ReflectionUtils.isAnnotationPresentFullCheck(clazz, Primary.class);
  }
}
