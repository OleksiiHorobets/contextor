package ua.gorobeos.contextor.context.element;

import java.lang.reflect.Constructor;
import java.util.Collection;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AnnotationElementDefinition extends ElementDefinition {

  @Builder
  public AnnotationElementDefinition(String name, Class<?> type, Constructor<?> initConstructor, String scope,
      Collection<DependencyDefinition> dependencies) {
    super(name, type, initConstructor, scope, dependencies);
  }

}
