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
import ua.gorobeos.contextor.context.utils.ReflectionUtils;
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
      var res = ScannerUtils.scanPackage("ua.gorobeos.contextor.scanner.test.packs.one_lvl_deep");

      assertThat(res).hasSize(3)
          .containsExactlyInAnyOrder(ClassA.class, ClassB.class, InterfaceA.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "ua.gorobeos.contextor.scanner.test.--+++",
        "ua.gorobeos.contextor.scanner.test.my-package"
    })
    void shouldValidatePathBeforeScan(String invalidPackage) {
      assertThatThrownBy(() -> ScannerUtils.scanPackage(invalidPackage))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Invalid package name: " + invalidPackage);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldNotAcceptNullOrEmptyPath(String invalidPackage) {
      assertThatThrownBy(() -> ScannerUtils.scanPackage(invalidPackage))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Package path cannot be null or empty.");
    }
  }

}
