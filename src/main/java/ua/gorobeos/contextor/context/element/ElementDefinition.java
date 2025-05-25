package ua.gorobeos.contextor.context.element;


import java.lang.reflect.Constructor;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class ElementDefinition {

  public static final String SINGLETON_SCOPE = "singleton";
  public static final String PROTOTYPE_SCOPE = "prototype";

  protected String name;
  protected Class<?> type;
  protected Constructor<?> initConstructor;
  protected String scope;
  protected Collection<DependencyDefinition> dependencies;
}
