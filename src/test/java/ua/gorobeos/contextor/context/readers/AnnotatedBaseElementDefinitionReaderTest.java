package ua.gorobeos.contextor.context.readers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ua.gorobeos.contextor.context.element.ElementDefinition.PROTOTYPE_SCOPE;
import static ua.gorobeos.contextor.context.element.ElementDefinition.SINGLETON_SCOPE;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.gorobeos.contextor.context.annotations.Element;
import ua.gorobeos.contextor.context.annotations.Injected;
import ua.gorobeos.contextor.context.annotations.Primary;
import ua.gorobeos.contextor.context.annotations.Qualifier;
import ua.gorobeos.contextor.context.annotations.Scope;
import ua.gorobeos.contextor.context.element.DependencyDefinition;
import ua.gorobeos.contextor.context.exceptions.InvalidElementScopeException;
import ua.gorobeos.contextor.context.exceptions.MultipleConstructorCandidatesException;
import ua.gorobeos.contextor.context.storage.ElementDefinitionHolder;


@ExtendWith(MockitoExtension.class)
class AnnotatedBaseElementDefinitionReaderTest {

  @InjectMocks
  AnnotatedBaseElementDefinitionReader reader;
  @Mock
  ElementDefinitionHolder elementDefinitionHolder;

  @Nested
  @DisplayName("Element Scope Resolver Tests")
  class ResolveScopeTest {

    @Test
    void shouldApplyDefaultSingletonScopeWhenNoSpecified() {
      String scope = reader.resolveScope(Object.class);
      assertEquals("singleton", scope, "Default scope should be 'singleton'");
    }

    @Test
    void shouldApplySpecifiedScopeWhenExplicitlySet() {
      String scope = reader.resolveScope(PrototypeClass.class);
      assertEquals(PROTOTYPE_SCOPE, scope, "Scope should be 'prototype' for PrototypeClass");
    }

    @Test
    void shouldThrowInvalidScopeExceptionWhenInvalidScope() {
      assertThatThrownBy(() -> reader.resolveScope(InvalidScopeClass.class))
          .isInstanceOf(InvalidElementScopeException.class)
          .hasMessageContaining("Invalid scope name provided: invalid_scope. Valid scopes are: ");
    }

    @Scope(PROTOTYPE_SCOPE)
    private static class PrototypeClass {

    }

    @Scope("invalid_scope")
    private static class InvalidScopeClass {

    }
  }

  @Nested
  @DisplayName("Element Name Resolver Tests")
  class ResolveElementName {

    @Test
    void shouldUseDefaultNamingConventionForUnnamedClass() {
      String elementName = reader.resolveElementName(UnnamedClass.class);
      assertEquals("unnamedClass", elementName, "Default name should be 'unnamedClass'");
    }

    @Test
    void shouldUseCustomNameWhenAnnotated() {
      String elementName = reader.resolveElementName(CustomNamedClass.class);
      assertEquals("customNamedElement", elementName, "Custom name should be 'customNamedElement'");
    }


    @Element("customNamedElement")
    private static class CustomNamedClass {

    }

    @Element
    private static class UnnamedClass {

    }
  }

  @Nested
  @DisplayName("Resolve Elements Constructor Tests")
  class ResolveElementsConstructor {

    @Test
    void shouldGetDefaultConstructorIfOnlyOnePresent() {
      var constructor = reader.resolveElementInitConstructor(SingleConstructorClass.class);
      assertEquals(SingleConstructorClass.class.getDeclaredConstructors()[0], constructor,
          "Should return the only constructor available");
    }

    @Test
    void shouldThrowMultipleConstructorCandidatesException() {
      assertThatThrownBy(() -> reader.resolveElementInitConstructor(MultipleNotAnnotatedConstructorsClass.class))
          .isInstanceOf(MultipleConstructorCandidatesException.class)
          .hasMessageContaining(
              "Could not resolve multiple non-annotated constructors for class: ");
    }

    @Test
    void shouldPrioritizeInjectedConstructorWhenSingleInjectedConstructorAvailable() {
      var constructor = reader.resolveElementInitConstructor(SingleInjectedConstructorClass.class);
      assertEquals(SingleInjectedConstructorClass.class.getDeclaredConstructors()[0], constructor,
          "Should return the injected constructor");
    }

    @Test
    void shouldPrioritizeInjectedConstructorWhenMultipleAreAvailable() {
      var constructor = reader.resolveElementInitConstructor(MultipleConstructorsWithOneInjectedClass.class);
      assertEquals(MultipleConstructorsWithOneInjectedClass.class.getDeclaredConstructors()[1], constructor,
          "Should return the injected constructor when multiple constructors are present");
    }

    @Test
    void shouldThrowMultipleConstructorCandidatesExceptionWhenMultipleInjectedConstructorsArePresent() {
      assertThatThrownBy(() -> reader.resolveElementInitConstructor(MultipleInjectedConstructors.class))
          .isInstanceOf(MultipleConstructorCandidatesException.class)
          .hasMessageContaining(
              "Multiple @Injected constructors found for class: ");
    }

    private static class SingleConstructorClass {

      public SingleConstructorClass() {
        // Default constructor
      }
    }

    private static class MultipleNotAnnotatedConstructorsClass {

      public MultipleNotAnnotatedConstructorsClass() {
        // Default constructor
      }

      public MultipleNotAnnotatedConstructorsClass(String param) {
        // Another constructor
      }
    }

    private static class SingleInjectedConstructorClass {

      @Injected
      public SingleInjectedConstructorClass(String param) {
      }
    }

    private static class MultipleConstructorsWithOneInjectedClass {

      public MultipleConstructorsWithOneInjectedClass(String param) {
      }

      @Injected
      public MultipleConstructorsWithOneInjectedClass(String param, int number) {
      }
    }


    private static class MultipleInjectedConstructors {

      @Injected
      public MultipleInjectedConstructors(String param) {
      }

      @Injected
      public MultipleInjectedConstructors(String param, int number) {
      }
    }
  }

  @Nested
  @DisplayName("Resolve Dependencies From Constructor Tests")
  class ResolveDependenciesFromConstructor {

    @Test
    void shouldReturnEmptyListWhenConstructorWithNoParametersProvided() {
      var constructor = reader.resolveElementInitConstructor(EmptyConstructorClass.class);
      var dependencies = reader.resolveDependencies(constructor);
      assertThat(dependencies).isEmpty();
    }

    @Test
    void shouldResolveNonAnnotatedDependenciesFromConstructor() {
      var constructor = reader.resolveElementInitConstructor(NonAnnotatedDependenciesClass.class);
      var dependencies = reader.resolveDependencies(constructor);
      assertThat(dependencies)
          .hasSize(2)
          .containsExactlyInAnyOrder(
              DependencyDefinition.builder()
                  .name("elementName")
                  .clazz(String.class)
                  .build(),
              DependencyDefinition.builder()
                  .name("numberDependency")
                  .clazz(Integer.class)
                  .build()
          );
    }

    @Test
    void shouldSpecifyQualifiersWhenAnnotationsArePresent() {
      var constructor = reader.resolveElementInitConstructor(AnnotatedDependenciesClass.class);
      var dependencies = reader.resolveDependencies(constructor);
      assertThat(dependencies)
          .hasSize(2)
          .containsExactlyInAnyOrder(
              DependencyDefinition.builder()
                  .name("elementName")
                  .qualifier("specifiedElementName")
                  .clazz(String.class)
                  .build(),
              DependencyDefinition.builder()
                  .name("numberDependency")
                  .qualifier("specifiedNumberDependency")
                  .clazz(Integer.class)
                  .build()
          );
    }


    private static class EmptyConstructorClass {

      public EmptyConstructorClass() {
      }
    }

    private static class NonAnnotatedDependenciesClass {

      public NonAnnotatedDependenciesClass(String elementName, Integer numberDependency) {
      }
    }

    private static class AnnotatedDependenciesClass {

      public AnnotatedDependenciesClass(@Qualifier("specifiedElementName") String elementName,
          @Qualifier("specifiedNumberDependency") Integer numberDependency) {
      }
    }


  }

  @Nested
  @DisplayName("Complete Element Definition Test")
  class FullElementDefinitionTest {

    @Test
    void shouldCreateElementDefinitionCorrectly() {
      var elementDefinition = reader.readElementDefinition(TestElement.class);

      assertThat(elementDefinition).isNotNull();
      assertAll(
          () -> assertThat(elementDefinition.getName()).isEqualTo("specifiedName"),
          () -> assertThat(elementDefinition.getType()).isEqualTo(TestElement.class),
          () -> assertThat(elementDefinition.getScope()).isEqualTo(SINGLETON_SCOPE),
          () -> assertThat(elementDefinition.getIsPrimary()).isTrue(),
          () -> assertThat(elementDefinition.getInitConstructor())
              .isEqualTo(TestElement.class.getDeclaredConstructors()[1])
      );

      var dependencies = elementDefinition.getDependencies();
      assertThat(dependencies).hasSize(2)
          .containsExactlyInAnyOrder(
              DependencyDefinition.builder()
                  .name("strParam")
                  .qualifier("specifiedStrParam")
                  .clazz(String.class)
                  .build(),
              DependencyDefinition.builder()
                  .name("value")
                  .clazz(Integer.class)
                  .build()
          );
    }

    @Element("specifiedName")
    @Primary
    private static class TestElement {

      public TestElement(String strPram) {
      }

      @Injected
      public TestElement(@Qualifier("specifiedStrParam") String strParam, Integer value) {
      }
    }
  }
}