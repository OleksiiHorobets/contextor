package ua.gorobeos.contextor.context.storage.context_full_load.circular_dependency.simple_circ_dependency;

import ua.gorobeos.contextor.context.annotations.Element;

@Element
public class B {

  private final A a;

  public B(A a) {
    this.a = a;
  }

}
