package ua.gorobeos.contextor.context.storage.context_full_load.circular_dependency.multi_dependencies;

import lombok.RequiredArgsConstructor;
import ua.gorobeos.contextor.context.annotations.Element;

@Element
@RequiredArgsConstructor
class BImpl implements BInterface {

  private final CInterface c;

}
