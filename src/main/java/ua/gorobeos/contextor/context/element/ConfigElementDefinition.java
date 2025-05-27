package ua.gorobeos.contextor.context.element;

import java.lang.reflect.Constructor;
import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ua.gorobeos.contextor.context.dependencies.DependencyDefinition;

@NoArgsConstructor
public class ConfigElementDefinition extends ElementDefinition {

  @Getter
  private Collection<ElementDefinition> methodDefinedElements;

  @Builder
  public ConfigElementDefinition(String name, Boolean isPrimary, Class<?> type, Constructor<?> initConstructor, String scope,
      Collection<DependencyDefinition> dependencies, Collection<ElementDefinition> methodDefinedElements) {
    super(name, isPrimary, type, initConstructor, scope, dependencies);
    this.methodDefinedElements = methodDefinedElements;
  }
}
