package ua.gorobeos.contextor.context.storage.context_full_load.circular_dependency.complex_circ_dependency;

import lombok.RequiredArgsConstructor;
import ua.gorobeos.contextor.context.annotations.Element;

@Element
@RequiredArgsConstructor
public class CImpl implements CInterface {

  private final AInterface a;


}
