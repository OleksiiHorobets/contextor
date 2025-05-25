package ua.gorobeos.contextor.context.storage;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static ua.gorobeos.contextor.context.element.ElementDefinition.SINGLETON_SCOPE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ua.gorobeos.contextor.context.element.AnnotationElementDefinition;
import ua.gorobeos.contextor.context.element.ElementDefinition;
import ua.gorobeos.contextor.context.exceptions.ElementNameConflictException;

class DefaultElementDefinitionHolderTest {

  DefaultElementDefinitionHolder holder;

  @BeforeEach
  void setUp() {
    holder = new DefaultElementDefinitionHolder();
  }


  @Nested
  @DisplayName("Adding Element Definitions")
  class AddNewDefinitionTest {

    @Test
    void shouldAddNewDefinitionOfNoneExists() {
      ElementDefinition definition = AnnotationElementDefinition.builder()
          .name("testElement")
          .scope(SINGLETON_SCOPE)
          .build();

      holder.addElementDefinition(definition);

      assertThat(holder.getElementDefinitionMap())
          .hasSize(1)
          .containsKey(definition.getName())
          .containsValue(definition);
    }

    @Test
    void shouldThrowExceptionIfSuchElementNameAlreadyExists() {
      ElementDefinition definition = AnnotationElementDefinition.builder()
          .name("testElement")
          .scope(SINGLETON_SCOPE)
          .build();

      holder.addElementDefinition(definition);

      ElementDefinition duplicateDefinition = AnnotationElementDefinition.builder()
          .name("testElement")
          .scope(SINGLETON_SCOPE)
          .build();

      assertThatThrownBy(() -> holder.addElementDefinition(duplicateDefinition))
          .isInstanceOf(ElementNameConflictException.class)
          .hasMessageContaining("Element definition with name 'testElement' already exists");
    }
  }
}