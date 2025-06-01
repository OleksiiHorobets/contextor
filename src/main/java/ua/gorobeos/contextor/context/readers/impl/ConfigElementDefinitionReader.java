package ua.gorobeos.contextor.context.readers.impl;


import static ch.qos.logback.core.util.StringUtil.lowercaseFirstLetter;
import static ua.gorobeos.contextor.context.element.ElementDefinition.SINGLETON_SCOPE;
import static ua.gorobeos.contextor.context.utils.ReflectionUtils.*;
import static ua.gorobeos.contextor.context.utils.ReflectionUtils.getSingleAnnotationFromMethod;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import ua.gorobeos.contextor.context.annotations.ExternalElement;
import ua.gorobeos.contextor.context.annotations.Primary;
import ua.gorobeos.contextor.context.annotations.Scope;
import ua.gorobeos.contextor.context.element.ConfigElementDefinition;
import ua.gorobeos.contextor.context.element.ElementDefinition;
import ua.gorobeos.contextor.context.element.MethodDefinedElementDefinition;
import ua.gorobeos.contextor.context.exceptions.InvalidElementScopeException;
import ua.gorobeos.contextor.context.readers.BaseElementDefinitionReader;
import ua.gorobeos.contextor.context.utils.ReflectionUtils;

@Slf4j
public class ConfigElementDefinitionReader extends BaseElementDefinitionReader {

  @Override
  protected String resolveScope(Class<?> clazz) {
    if (isAnnotationPresentFullCheck(clazz, Scope.class)) {
      throw new InvalidElementScopeException(
          "Config classes are always 'singleton'. Please remove the @Scope annotation from class: " + clazz.getName());
    }
    return SINGLETON_SCOPE;
  }

  @Override
  protected String resolveElementName(Class<?> clazz) {
    return getDefaultElementName(clazz);
  }

  @Override
  protected ElementDefinition getElementDefinitionContainerWithSpecifics(Class<?> clazz) {
    return ConfigElementDefinition.builder().isPrimary(true).methodDefinedElements(getDefinedElementsFromConfig(clazz)).build();
  }

  private Collection<ElementDefinition> getDefinedElementsFromConfig(Class<?> clazz) {

    return getMethodsAnnotatedBy(clazz, ExternalElement.class).stream().map(this::mapMethodToElementDefinition).map(definition -> {
      definition.setConfigClass(clazz);
      return definition;
    }).collect(Collectors.toSet());
  }

  private MethodDefinedElementDefinition mapMethodToElementDefinition(Method method) {
    log.debug("Mapping method {} to ElementDefinition", method.getName());
    var elementDefinition = MethodDefinedElementDefinition.builder().name(lowercaseFirstLetter(method.getName()))
        .isPrimary(getSingleAnnotationFromMethod(method, Primary.class).isPresent()).type(method.getReturnType())
        .scope(getValueFromAnnotationOnMethod(method, Scope.class, "value", String.class)
            .map(scope -> {
              if (!VALID_SCOPES.contains(scope)) {
                throw new IllegalArgumentException(
                    "Invalid scope name provided: " + scope + " for method: " + method.getName());
              }
              return scope;
            })
            .orElseGet(() -> {
          log.info("No scope defined for method: {}. Defaulting to singleton scope.", method.getName());
          return SINGLETON_SCOPE;
        })).dependencies(mapParametersToDependencyDefinitions(method.getParameters())).initMethod(method).build();

    log.debug("ElementDefinition created for method {}: {}", method.getName(), elementDefinition);
    return elementDefinition;
  }

}
