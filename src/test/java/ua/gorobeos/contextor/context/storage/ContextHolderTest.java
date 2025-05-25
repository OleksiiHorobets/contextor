package ua.gorobeos.contextor.context.storage;

import org.junit.jupiter.api.Test;
import ua.gorobeos.contextor.context.annotations.ElementScan;

class ContextHolderTest {

  @Test
  void shouldInitializeContextCorrectly() {
    var context = ContextHolder.initializeContext(ContextMainClass.class);

    context.getElementDefinitionHolder().getElementDefinitions();
    context.getElement("aFirst");
  }

  @ElementScan(
      basePackages = "ua.gorobeos.contextor.context.storage.context_full_load.lvl_one"
  )
  private static class ContextMainClass {

  }
}