package ua.gorobeos.contextor.context.storage.context_full_load.with_config;

import lombok.RequiredArgsConstructor;
import ua.gorobeos.contextor.context.annotations.ContextConfig;
import ua.gorobeos.contextor.context.annotations.ExternalElement;
import ua.gorobeos.contextor.context.annotations.Scope;

@ContextConfig
@RequiredArgsConstructor
public class ConfigClass {

  private final BookRepository bookRepository;

  @ExternalElement
  @Scope("Primary")
  public String noDependencyExternalElement() {
    System.out.println("--------- creating again external element with no dependencies");
    return "External Element";
  }

  @ExternalElement
  @Scope("prototype")
  public String singleDependencyExternalElement(String noDependencyExternalElement) {
    System.out.println("--------- creating again external element with single dependency: " + noDependencyExternalElement);
    return "Another with dependency: " + noDependencyExternalElement;
  }
}
