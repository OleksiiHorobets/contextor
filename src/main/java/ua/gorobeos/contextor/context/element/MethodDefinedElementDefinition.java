package ua.gorobeos.contextor.context.element;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.gorobeos.contextor.context.dependencies.DependencyDefinition;

@NoArgsConstructor
@Setter
@Getter
public class MethodDefinedElementDefinition extends ElementDefinition {

  private Class<?> configClass;
  private Method initMethod;

  @Builder
  public MethodDefinedElementDefinition(String name, Boolean isPrimary, Class<?> type, Constructor<?> initConstructor, String scope,
      Collection<DependencyDefinition> dependencies, Class<?> configClass, Method initMethod) {
    super(name, isPrimary, type, initConstructor, scope, dependencies);
    this.configClass = configClass;
    this.initMethod = initMethod;
  }
}
