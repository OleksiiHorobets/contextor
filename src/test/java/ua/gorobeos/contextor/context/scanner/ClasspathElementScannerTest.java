package ua.gorobeos.contextor.context.scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.gorobeos.contextor.context.annotations.ElementScan;
import ua.gorobeos.contextor.context.exceptions.ContainerInitException;
import ua.gorobeos.contextor.scanner.test.packs.one_lvl_deep.ClassA;
import ua.gorobeos.contextor.scanner.test.packs.one_lvl_deep.ClassB;
import ua.gorobeos.contextor.scanner.test.packs.two_lvl_deep.ClassC;
import ua.gorobeos.contextor.scanner.test.packs.two_lvl_deep.ClassD;
import ua.gorobeos.contextor.scanner.test.packs.two_lvl_deep.second_lvl.ClassE;

class ClasspathElementScannerTest {

  @Test
  @DisplayName("Test scanning of classpath elements with multiple packages")
  void shouldScanPackagesCorrectly() {
    var scannedClasses = ClasspathElementScanner.scanPackages(InitClass.class);
    assertThat(scannedClasses).isNotNull()
        .hasSize(5)
        .containsExactlyInAnyOrder(ClassA.class,
            ClassB.class,
            ClassC.class,
            ClassD.class,
            ClassE.class
        );
  }

  @Test
  @DisplayName("Should throw ContainerInitException when no packages specified")
  void shouldThrowExceptionWhenNoPackagesSpecified() {

    assertThatThrownBy(
        () -> ClasspathElementScanner.scanPackages(InitClassWithoutPackages.class)
    ).isInstanceOf(ContainerInitException.class)
        .hasMessageContaining(
            "No base packages specified in ElementScan annotation on class: ua.gorobeos.contextor.context.scanner.ClasspathElementScannerTest$InitClassWithoutPackages");
  }

  @Test
  @DisplayName("Should throw exception when invalid package specified")
  void shouldThrowExceptionWhenInvalidPackageSpecified() {
    assertThatThrownBy(
        () -> ClasspathElementScanner.scanPackages(InvalidPackagesInitClass.class)
    ).isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid package name: ");
  }

  @ElementScan(basePackages = {
      "ua.gorobeos.contextor.scanner.test.packs.one_lvl_deep",
      "ua.gorobeos.contextor.scanner.test.packs.two_lvl_deep"
  })
  class InitClass {

  }

  @ElementScan(basePackages = {})
  class InitClassWithoutPackages {

  }

  @ElementScan(basePackages = {"123+--", "123"})
  class InvalidPackagesInitClass {

  }
}