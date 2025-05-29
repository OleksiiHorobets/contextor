package ua.gorobeos.contextor.context.storage.context_full_load.circular_dependency.simple_circ_dependency;

import ua.gorobeos.contextor.context.annotations.Element;

@Element
public class A {

  private final B b;

  public A(B b) {
    this.b = b;
  }
}
