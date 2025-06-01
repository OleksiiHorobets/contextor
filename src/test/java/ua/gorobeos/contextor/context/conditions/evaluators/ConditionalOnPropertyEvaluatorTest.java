package ua.gorobeos.contextor.context.conditions.evaluators;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import ua.gorobeos.contextor.context.annotations.conditions.ConditionalOnProperty;
import ua.gorobeos.contextor.context.conditions.ConditionalContext;
import ua.gorobeos.contextor.context.config.ConfigurationReader;

class ConditionalOnPropertyEvaluatorTest {

  private final ConditionalOnPropertyEvaluator evaluator = new ConditionalOnPropertyEvaluator();

  @Test
  @DisplayName("Should fail when property exists but value is null")
  void shouldFailWhenPropertyExistsButValueIsNull() {
    @ConditionalOnProperty(name = "existing.property", value = "expectedValue")
    class TestClass {

    }

    try (MockedStatic<ConfigurationReader> mockedConfigReader = mockStatic(ConfigurationReader.class)) {
      mockedConfigReader.when(() -> ConfigurationReader.getOrDefault("existing.property", null))
          .thenReturn(null);

      var context = initializeConditionalContext(TestClass.class);

      var result = evaluator.evaluate(context);

      assertThat(result.isConditionalCheckPassed()).isFalse();
      assertThat(result.getConditionalCheckResults())
          .contains("Property 'existing.property' with value 'expectedValue' does not exist, evaluation failed");
    }
  }

  @Test
  @DisplayName("Should fail when property exists but is empty")
  void shouldFailWhenPropertyExistsButIsEmpty() {
    @ConditionalOnProperty(name = "existing.property", value = "expectedValue")
    class TestClass {

    }

    try (MockedStatic<ConfigurationReader> mockedConfigReader = mockStatic(ConfigurationReader.class)) {
      mockedConfigReader.when(() -> ConfigurationReader.getOrDefault("existing.property", null))
          .thenReturn("");

      var context = initializeConditionalContext(TestClass.class);

      var result = evaluator.evaluate(context);

      assertThat(result.isConditionalCheckPassed()).isFalse();
      assertThat(result.getConditionalCheckResults())
          .contains("Property 'existing.property' with value 'expectedValue' does not exist, evaluation failed");
    }
  }

  @Test
  @DisplayName("Should fail when annotation has valid name but invalid value")
  void shouldFailWhenAnnotationHasValidNameButInvalidValue() {
    @ConditionalOnProperty(name = "existing.property", value = "wrongValue")
    class TestClass {

    }

    try (MockedStatic<ConfigurationReader> mockedConfigReader = mockStatic(ConfigurationReader.class)) {
      mockedConfigReader.when(() -> ConfigurationReader.getOrDefault("existing.property", null))
          .thenReturn("correctValue");

      var context = initializeConditionalContext(TestClass.class);

      var result = evaluator.evaluate(context);

      assertThat(result.isConditionalCheckPassed()).isFalse();
      assertThat(result.getConditionalCheckResults())
          .contains("Property 'existing.property' with value 'wrongValue' does not exist, evaluation failed");
    }
  }

  @Test
  @DisplayName("Should fail when annotation has valid value but invalid name")
  void shouldFailWhenAnnotationHasValidValueButInvalidName() {
    @ConditionalOnProperty(name = "nonexistent.property", value = "expectedValue")
    class TestClass {

    }

    try (MockedStatic<ConfigurationReader> mockedConfigReader = mockStatic(ConfigurationReader.class)) {
      mockedConfigReader.when(() -> ConfigurationReader.getOrDefault("nonexistent.property", null))
          .thenReturn(null);

      var context = initializeConditionalContext(TestClass.class);

      var result = evaluator.evaluate(context);

      assertThat(result.isConditionalCheckPassed()).isFalse();
      assertThat(result.getConditionalCheckResults())
          .contains("Property 'nonexistent.property' with value 'expectedValue' does not exist, evaluation failed");
    }
  }

  @Test
  @DisplayName("Should return context unchanged when element class is null")
  void shouldReturnContextUnchangedWhenElementClassIsNull() {
    var context = initializeConditionalContext(null);

    var result = evaluator.evaluate(context);

    assertThat(result).isSameAs(context);
  }

  @Test
  @DisplayName("Should return context unchanged when ConditionalOnProperty annotation is not present")
  void shouldReturnContextUnchangedWhenAnnotationNotPresent() {
    class TestClass {

    }

    var context = initializeConditionalContext(TestClass.class);

    var result = evaluator.evaluate(context);

    assertThat(result).isSameAs(context);
  }

  @Test
  @DisplayName("Should fail when ConditionalOnProperty annotation is missing 'name' or 'value'")
  void shouldFailWhenAnnotationMissingNameOrValue() {
    @ConditionalOnProperty(name = "", value = "")
    class TestClass {

    }

    var context = initializeConditionalContext(TestClass.class);

    var result = evaluator.evaluate(context);

    assertThat(result.isConditionalCheckPassed()).isFalse();
    assertThat(result.getConditionalCheckResults())
        .hasSize(1);
  }

  private static ConditionalContext initializeConditionalContext(Class<?> elementClass) {
    return ConditionalContext.builder()
        .elementClass(elementClass)
        .isCircuitBreaker(true)
        .conditionalCheckResults(new ArrayList<>())
        .conditionalCheckPassed(true)
        .build();
  }
}