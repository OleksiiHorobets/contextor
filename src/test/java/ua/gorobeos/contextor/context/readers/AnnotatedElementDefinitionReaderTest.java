package ua.gorobeos.contextor.context.readers;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ua.gorobeos.contextor.context.element.ElementDefinition.PROTOTYPE_SCOPE;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.gorobeos.contextor.context.annotations.Element;
import ua.gorobeos.contextor.context.annotations.Scope;
import ua.gorobeos.contextor.context.exceptions.InvalidElementScopeException;
import ua.gorobeos.contextor.context.storage.ElementDefinitionHolder;


@ExtendWith(MockitoExtension.class)
class AnnotatedElementDefinitionReaderTest {

  @InjectMocks
  AnnotatedElementDefinitionReader reader;
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

    private static class UnnamedClass {

    }
  }


}