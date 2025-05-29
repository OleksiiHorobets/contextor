package ua.gorobeos.contextor.context.storage.context_full_load.circular_dependency.one_interface;

import lombok.RequiredArgsConstructor;
import ua.gorobeos.contextor.context.annotations.Element;

@Element
@RequiredArgsConstructor
class BImpl implements BInterface {

  private final AInterface a;

}
