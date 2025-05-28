package ua.gorobeos.contextor.context.storage.context_full_load.conditional.file;

import lombok.Getter;
import ua.gorobeos.contextor.context.annotations.Element;

@Element
public class DependentClass {

  @Getter
  private final FileConditional fileConditional;

  public DependentClass(FileConditional fileConditional) {
    this.fileConditional = fileConditional;
  }
}
