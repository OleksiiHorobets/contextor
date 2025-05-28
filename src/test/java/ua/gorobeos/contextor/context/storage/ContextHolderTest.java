package ua.gorobeos.contextor.context.storage;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ua.gorobeos.contextor.context.annotations.ElementScan;
import ua.gorobeos.contextor.context.storage.context_full_load.conditional.file.DependentClass;
import ua.gorobeos.contextor.context.storage.context_full_load.conditional.file.SecondOnFileCondition;

class ContextHolderTest {

  @Test
  void shouldInitializeContextCorrectly() {
    var context = ContextHolder.initializeContext(ContextMainClass.class);

    context.getElementDefinitionHolder().getElementDefinitions();
    var res = context.getElement("aFirst");
    assertThat(res).isNotEmpty();
  }

  @Test
  void shouldReturnSameObjectForSingleton() {
    var context = ContextHolder.initializeContext(ContextWithConfigClass.class);

    context.getElementDefinitionHolder().getElementDefinitions();
    var singletonElementFirstCall = context.getElement("noDependencyExternalElement");
    var singletonElementSecondCall = context.getElement("noDependencyExternalElement");

    assertThat(singletonElementFirstCall)
        .isNotEmpty()
        .get()
        .isSameAs(singletonElementSecondCall.get());
  }

  @Test
  void shouldReturnNewObjectForPrototype() {
    var context = ContextHolder.initializeContext(ContextWithConfigClass.class);

    context.getElementDefinitionHolder().getElementDefinitions();
    var prototypeElementFirstCall = context.getElement("singleDependencyExternalElement");
    var prototypeElementSecondCall = context.getElement("singleDependencyExternalElement");

    assertThat(prototypeElementFirstCall)
        .isNotEmpty();
    assertThat(prototypeElementSecondCall)
        .isNotEmpty()
        .get()
        .isNotSameAs(prototypeElementFirstCall);
  }

  @Nested
  class FileConditionalTest {

    @Test
    void shouldLoadContextWithFileCondition() {
      var context = ContextHolder.initializeContext(FileConditional.class);

      var res = context.getElement("dependentClass");
      assertThat(res).isNotEmpty()
          .get()
          .isInstanceOfSatisfying(DependentClass.class,
              dependentClass -> assertThat(dependentClass.getFileConditional()).isInstanceOf(SecondOnFileCondition.class));
    }

  }

  @Nested
  class SystemTestConditional {

    @Test
    void shouldLoadContextWithOsCondition() {
      var context = ContextHolder.initializeContext(OsConditional.class);

      var windowsElement = context.getElement("windowsElement");
      var linuxElement = context.getElement("linuxElement");

      assertThat(windowsElement).isNotEmpty();
      assertThat(linuxElement).isEmpty();
    }

  }


  @Nested
  class WebConditionalTest {

    @Test
    void shouldLoadContextWithWebCondition() {
      var context = ContextHolder.initializeContext(WebConditional.class);

      var res = context.getElement("webDependentElement");
      assertThat(res).isNotEmpty()
          .get()
          .isNotNull();
      assertThat(context.getElement("failedWebDependentElement"))
          .isEmpty();
    }


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

  @ElementScan(
      basePackages = "ua.gorobeos.contextor.context.storage.context_full_load.conditional.file"
  )
  private static class FileConditional {

  }

  @ElementScan(
      basePackages = "ua.gorobeos.contextor.context.storage.context_full_load.conditional.os"
  )
  private static class OsConditional {

  }

  @ElementScan(
      basePackages = "ua.gorobeos.contextor.context.storage.context_full_load.conditional.web"
  )
  private static class WebConditional {

  }
}