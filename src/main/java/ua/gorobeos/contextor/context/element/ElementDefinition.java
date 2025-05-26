package ua.gorobeos.contextor.context.element;


import java.lang.reflect.Constructor;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.gorobeos.contextor.context.dependencies.DependencyDefinition;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class ElementDefinition {

  public static final String SINGLETON_SCOPE = "singleton";
  public static final String PROTOTYPE_SCOPE = "prototype";

  protected String name;
  protected Boolean isPrimary;
  protected Class<?> type;
  protected Constructor<?> initConstructor;
  protected String scope;
  protected Collection<DependencyDefinition> dependencies;
}
