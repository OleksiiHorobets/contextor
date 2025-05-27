package ua.gorobeos.contextor.context.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ua.gorobeos.contextor.context.element.ElementDefinition.PROTOTYPE_SCOPE;

import java.io.Serializable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ua.gorobeos.contextor.context.annotations.ContextConfig;
import ua.gorobeos.contextor.context.annotations.Element;
import ua.gorobeos.contextor.context.annotations.ElementScan;
import ua.gorobeos.contextor.context.annotations.ExternalElement;
import ua.gorobeos.contextor.context.annotations.Scope;

class ReflectionUtilsTest {

  @Nested
  @DisplayName("Tests annotation presence check")
  class AnnotationPresenceTest {

    @Test
    void shouldFindAnnotationInFlatClass() {
      var res = ReflectionUtils.isAnnotationPresentFullCheck(FlatElement.class, Element.class);
      assertThat(res).isTrue();
    }

    @Test
    void shouldFindAnnotationInClassHierarchy() {
      var res = ReflectionUtils.isAnnotationPresentFullCheck(ConcreteElementLvlOne.class, Element.class);
      assertThat(res).isTrue();
    }

    @Test
    void shouldNotFindAnnotationInClassWithoutIt() {
      var res = ReflectionUtils.isAnnotationPresentFullCheck(NonAnnotatedClass.class, Element.class);
      assertThat(res).isFalse();
    }
  }

  @Nested
  @DisplayName("Tests annotation retrieval from class")
  class AnnotationRetrieval {

    @Test
    void shouldGetAnnotationsFromFlatClass() {
      var res = ReflectionUtils.getAnnotationsFromClass(FlatElement.class);
      assertThat(res).hasSize(1).contains(Element.class);
    }

    @Test
    void shouldGetAnnotationsFromAbstractClass() {
      var res = ReflectionUtils.getAnnotationsFromClass(AbstractElement.class);
      assertThat(res).hasSize(1).contains(Element.class);
    }

    @ParameterizedTest
    @ValueSource(classes = {
        ConcreteElementLvlOne.class,
        ConcreteElementLvlTwo.class
    })
    void shouldGetAnnotationsFromConcreteClass(Class<?> targetClazz) {
      var res = ReflectionUtils.getAnnotationsFromClass(targetClazz);
      assertThat(res).hasSize(1).contains(Element.class);
    }

    @Test
    void shouldReturnEmptySetForNonAnnotatedClass() {
      var res = ReflectionUtils.getAnnotationsFromClass(NonAnnotatedClass.class);
      assertThat(res).isEmpty();
    }

  }

  @Nested
  @DisplayName("Operations with annotations tests")
  class Annotations {

    @Test
    void shouldRetrieveValueFromAnnotationCorrectly() {
      var res = ReflectionUtils.getValueFromAnnotation(ElementInterface.class, ElementScan.class, "basePackages", String[].class);
      assertThat(res).isPresent();
      assertThat(res.get()).isInstanceOf(String[].class);
      assertThat(res.get()).containsExactly("ua.gorobeos.contextor.scanner.test.packs.one_lvl_deep",
          "ua.gorobeos.contextor.scanner.test.packs.two_lvl_deep");
    }
  }

  @Nested
  @DisplayName("Fetch annotated methods ")
  class AnnotatedMethods {

    @Test
    void shouldThrowIllegalArgumentExceptionIfNullClassProvided() {
      assertThatThrownBy(() -> ReflectionUtils.getMethodsAnnotatedBy(null, ExternalElement.class))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Class cannot be null");
    }

    @Test
    void shouldThrowIllegalArgumentExceptionIfNullAnnotationProvided() {
      assertThatThrownBy(() -> ReflectionUtils.getMethodsAnnotatedBy(ExternalElement.class, null))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Annotation cannot be null");
    }

    @Test
    void shouldGetMethodsOfClassByAnnotationCorrectly() {
      var methods = ReflectionUtils.getMethodsAnnotatedBy(ConfigClass.class, ExternalElement.class);
      assertThat(methods).hasSize(2)
          .extracting("name")
          .containsExactlyInAnyOrder("noArgsElement", "argsElement");
    }

  }

  @ContextConfig
  static class ConfigClass {

    @ExternalElement
    public String noArgsElement() {
      return new String();
    }

    @ExternalElement
    public String argsElement(FlatElement flatElement, String arg) {
      return flatElement.toString() + " " + arg;
    }

  }


  @Nested
  @DisplayName("Test for methods metadata fetching")
  class MethodsMetadata {

    @Test
    void shouldCorrectlyFetchMethodAnnotationData() throws Exception {
      var targetMethod = MethodClass.class.getDeclaredMethod("methodWithAnnotation", String.class);

      var actualValue = ReflectionUtils.getValueFromAnnotationOnMethod(targetMethod, Scope.class, "value", String.class);
      assertThat(actualValue)
          .isPresent()
          .contains(PROTOTYPE_SCOPE);
    }

    @Test
    void shouldReturnEmptyOptionalIfNoAnnotationFound() {
      var targetMethod = MethodClass.class.getDeclaredMethods()[0];

      var actualValue = ReflectionUtils.getValueFromAnnotationOnMethod(targetMethod, Element.class, "value", String.class);
      assertThat(actualValue).isEmpty();
    }

    private static class MethodClass {

      @ExternalElement
      @Scope(PROTOTYPE_SCOPE)
      public String methodWithAnnotation(String arg) {
        return arg;
      }
    }
  }

  @Element
  static class FlatElement {

  }

  @ElementScan(basePackages = {"ua.gorobeos.contextor.scanner.test.packs.one_lvl_deep", "ua.gorobeos.contextor.scanner.test.packs.two_lvl_deep"})
  static interface ElementInterface {

  }

  @Element
  static class AbstractElement implements ElementInterface {

  }

  static class ConcreteElementLvlOne extends AbstractElement {

  }

  static class ConcreteElementLvlTwo extends ConcreteElementLvlOne implements Serializable {

  }

  static class NonAnnotatedClass {

  }
}