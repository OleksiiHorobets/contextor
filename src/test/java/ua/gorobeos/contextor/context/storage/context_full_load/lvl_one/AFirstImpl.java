package ua.gorobeos.contextor.context.storage.context_full_load.lvl_one;

import ua.gorobeos.contextor.context.annotations.Element;

@Element("aFirst")
public class AFirstImpl implements InterfaceA {

  DependencyForAFirst dependencyForAFirst;

  public AFirstImpl(DependencyForAFirst dependencyForAFirst) {
    this.dependencyForAFirst = dependencyForAFirst;
  }
}
