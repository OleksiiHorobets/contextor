package ua.gorobeos.contextor.context.storage;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import ua.gorobeos.contextor.context.annotations.ElementScan;

class ContextHolderTest {

  @Test
  void shouldInitializeContextCorrectly() {
    var context = ContextHolder.initializeContext(ContextMainClass.class);

    context.getElementDefinitionHolder().getElementDefinitions();
    var res = context.getElement("aFirst");
    assertThat(res).isNotNull();
  }

  @Test
  void shouldReturnSameObjectForSingleton() {
    var context = ContextHolder.initializeContext(ContextWithConfigClass.class);

    context.getElementDefinitionHolder().getElementDefinitions();
    var singletonElementFirstCall = context.getElement("noDependencyExternalElement");
    var singletonElementSecondCall = context.getElement("noDependencyExternalElement");

    assertThat(singletonElementFirstCall)
        .isNotNull()
        .isSameAs(singletonElementSecondCall);
  }

  @Test
  void shouldReturnNewObjectForPrototype() {
    var context = ContextHolder.initializeContext(ContextWithConfigClass.class);

    context.getElementDefinitionHolder().getElementDefinitions();
    var prototypeElementFirstCall = context.getElement("singleDependencyExternalElement");
    var prototypeElementSecondCall = context.getElement("singleDependencyExternalElement");

    assertThat(prototypeElementFirstCall)
        .isNotNull();
    assertThat(prototypeElementSecondCall)
        .isNotNull()
        .isNotSameAs(prototypeElementFirstCall);
  }

  @ElementScan(
      basePackages = "ua.gorobeos.contextor.context.storage.context_full_load.lvl_one"
  )
  private static class ContextMainClass {

  }


  @ElementScan(
      basePackages = "ua.gorobeos.contextor.context.storage.context_full_load.with_config"
  )
  private static class ContextWithConfigClass {

  }
}