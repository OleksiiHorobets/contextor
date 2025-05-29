package ua.gorobeos.contextor.context.element;

import java.lang.reflect.Constructor;
import java.util.Collection;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ua.gorobeos.contextor.context.dependencies.DependencyDefinition;

@NoArgsConstructor
@ToString
public class AnnotationElementDefinition extends ElementDefinition {

  @Builder
  public AnnotationElementDefinition(String name, Boolean isPrimary, Class<?> type, Constructor<?> initConstructor, String scope,
      Collection<DependencyDefinition> dependencies) {
    super(name, isPrimary, type, initConstructor, scope, dependencies);
  }
}
