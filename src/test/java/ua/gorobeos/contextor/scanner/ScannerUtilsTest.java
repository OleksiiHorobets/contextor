package ua.gorobeos.contextor.scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.Serializable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import ua.gorobeos.contextor.context.annotations.Element;
import ua.gorobeos.contextor.context.annotations.ElementScan;
import ua.gorobeos.contextor.context.utils.ScannerUtils;
import ua.gorobeos.contextor.scanner.test.packs.one_lvl_deep.ClassA;
import ua.gorobeos.contextor.scanner.test.packs.one_lvl_deep.ClassB;
import ua.gorobeos.contextor.scanner.test.packs.one_lvl_deep.InterfaceA;


public class ScannerUtilsTest {

  @Nested
  @DisplayName("Tests package scan method")
  class PackageScanTest {

    @Test
    void shouldScanClassesTwoLevelDeep() {
      var res = ScannerUtils.scanClasses("ua.gorobeos.contextor.scanner.test.packs.one_lvl_deep");

      assertThat(res).hasSize(3)
          .containsExactlyInAnyOrder(ClassA.class, ClassB.class, InterfaceA.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "ua.gorobeos.contextor.scanner.test.--+++",
        "ua.gorobeos.contextor.scanner.test.my-package"
    })
    void shouldValidatePathBeforeScan(String invalidPackage) {
      assertThatThrownBy(() -> ScannerUtils.scanClasses(invalidPackage))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Invalid package name: " + invalidPackage);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldNotAcceptNullOrEmptyPath(String invalidPackage) {
      assertThatThrownBy(() -> ScannerUtils.scanClasses(invalidPackage))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Package path cannot be null or empty.");
    }
  }

  @Nested
  @DisplayName("Tests annotation presence check")
  class AnnotationPresenceTest {

    @Test
    void shouldFindAnnotationInFlatClass() {
      var res = ScannerUtils.isAnnotationPresentFullCheck(FlatElement.class, Element.class);
      assertThat(res).isTrue();
    }

    @Test
    void shouldFindAnnotationInClassHierarchy() {
      var res = ScannerUtils.isAnnotationPresentFullCheck(ConcreteElementLvlOne.class, Element.class);
      assertThat(res).isTrue();
    }

    @Test
    void shouldNotFindAnnotationInClassWithoutIt() {
      var res = ScannerUtils.isAnnotationPresentFullCheck(NonAnnotatedClass.class, Element.class);
      assertThat(res).isFalse();
    }
  }

  @Nested
  @DisplayName("Tests annotation retrieval from class")
  class AnnotationRetrieval {

    @Test
    void shouldGetAnnotationsFromFlatClass() {
      var res = ScannerUtils.getAnnotationsFromClass(FlatElement.class);
      assertThat(res).hasSize(1).contains(Element.class);
    }

    @Test
    void shouldGetAnnotationsFromAbstractClass() {
      var res = ScannerUtils.getAnnotationsFromClass(AbstractElement.class);
      assertThat(res).hasSize(1).contains(Element.class);
    }

    @ParameterizedTest
    @ValueSource(classes = {
        ConcreteElementLvlOne.class,
        ConcreteElementLvlTwo.class
    })
    void shouldGetAnnotationsFromConcreteClass(Class<?> targetClazz) {
      var res = ScannerUtils.getAnnotationsFromClass(targetClazz);
      assertThat(res).hasSize(1).contains(Element.class);
    }

    @Test
    void shouldReturnEmptySetForNonAnnotatedClass() {
      var res = ScannerUtils.getAnnotationsFromClass(NonAnnotatedClass.class);
      assertThat(res).isEmpty();
    }
  }

  @Element
  static class FlatElement {

  }
  @ElementScan(basePackages = "ua.gorobeos.contextor.scanner.test.packs.one_lvl_deep")
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

