package ua.gorobeos.contextor.context.dependencies;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ua.gorobeos.contextor.context.element.AnnotationElementDefinition;
import ua.gorobeos.contextor.context.element.ElementDefinition;
import ua.gorobeos.contextor.context.exceptions.UnresolvableDependencyException;
import ua.gorobeos.contextor.context.storage.DefaultElementDefinitionHolder;


class SimpleDependencyResolverTest {

  private DefaultElementDefinitionHolder elementDefinitionHolder;
  private SimpleDependencyResolver dependencyResolver;

  @BeforeEach
  void setUp() {
    elementDefinitionHolder = new DefaultElementDefinitionHolder();
    dependencyResolver = new SimpleDependencyResolver(elementDefinitionHolder);
  }

  @Nested
  @DisplayName("Should be able to resolve dependencies definition for class")
  class ResolveDependenciesDefinitionByClass {

    @Test
    @DisplayName("Should prioritize @Primary dependency when available")
    void shouldGetThePrimaryDependencyWhenAvailable() {
      var elementDefinitions = getElementDefinitions();
      elementDefinitions.get(3).setIsPrimary(true);
      elementDefinitions.forEach(elementDefinitionHolder::addElementDefinition);

      var dependencyElementDefinition = dependencyResolver.getDependencyDefinitionForClass(BookService.class);
      assertThat(dependencyElementDefinition).isNotNull()
          .extracting("name", "type", "isPrimary", "scope")
          .containsExactly(
              "thirdBookServiceImpl", ThirdBookServiceImpl.class, true, AnnotationElementDefinition.SINGLETON_SCOPE);
    }

    @Test
    @DisplayName("Should return dependency definition for class when single candidate found")
    void shouldReturnDependencyDefinitionForClassWhenSingleCandidateFound() {
      getElementDefinitions().subList(0, 2).forEach(elementDefinitionHolder::addElementDefinition);

      var dependencyElementDefinition = dependencyResolver.getDependencyDefinitionForClass(BookService.class);
      assertThat(dependencyElementDefinition).isNotNull()
          .extracting("name", "type", "isPrimary", "scope")
          .containsExactly(
              "firstBookServiceImpl", FirstBookServiceImpl.class, false, AnnotationElementDefinition.SINGLETON_SCOPE);
    }

    @Test
    @DisplayName("Should throw exception if multiple @Primary dependencies found")
    void shouldThrowUnresolvableDependencyExceptionWhenMultiplePrimaryCandidatesFound() {
      getElementDefinitions()
          .stream()
          .peek(dep -> dep.setIsPrimary(true))
          .forEach(elementDefinitionHolder::addElementDefinition);

      assertThatThrownBy(() -> dependencyResolver.getDependencyDefinitionForClass(BookService.class))
          .isInstanceOf(UnresolvableDependencyException.class)
          .hasMessageContaining("Multiple primary elements found for class");
    }

    @Test
    @DisplayName("Should throw exception if no candidates found")
    void shouldThrowExceptionWhenNoCandidatesFound() {
      getElementDefinitions().subList(0, 1).forEach(elementDefinitionHolder::addElementDefinition);

      assertThatThrownBy(() -> dependencyResolver.getDependencyDefinitionForClass(BookService.class))
          .isInstanceOf(UnresolvableDependencyException.class)
          .hasMessageContaining(
              "No element definition found for class 'ua.gorobeos.contextor.context.dependencies.SimpleDependencyResolverTest$BookService'");
    }

    @Test
    void shouldThrowUnresolvableDependencyExceptionWhenMultipleCandidatesFound() {

      getElementDefinitions().forEach(elementDefinitionHolder::addElementDefinition);

      assertThatThrownBy(() -> dependencyResolver.getDependencyDefinitionForClass(BookService.class))
          .isInstanceOf(UnresolvableDependencyException.class)
          .hasMessageContaining(
              "Multiple compatible elements found for class '%s'."
                  .formatted(BookService.class.getName()));
    }

  }

  @Nested
  @DisplayName("Resolve dependencies definition by name")
  class ResolveDependenciesDefinitionByName {

    @Test
    @DisplayName("Should return dependency definition by name no @Qualifier")
    void shouldReturnDependencyDefinitionByName() {
      getElementDefinitions().forEach(elementDefinitionHolder::addElementDefinition);

      ElementDefinition dependencyElementDefinition = dependencyResolver.retrieveDependency(
          new DependencyDefinition("firstBookServiceImpl", null, FirstBookServiceImpl.class));

      assertThat(dependencyElementDefinition).isNotNull()
          .extracting("name", "type", "isPrimary", "scope")
          .containsExactly(
              "firstBookServiceImpl", FirstBookServiceImpl.class, false, AnnotationElementDefinition.SINGLETON_SCOPE);
    }

    @Test
    void shouldThrowExceptionIfElementDefinitionIsIncompatible() {
      var elementDefinitions = getElementDefinitions();
      elementDefinitions.get(1).setType(String.class);
      elementDefinitions.get(1).setIsPrimary(true);
      elementDefinitions.forEach(elementDefinitionHolder::addElementDefinition);

      assertThatThrownBy(() -> dependencyResolver.retrieveDependency(
          new DependencyDefinition("firstBookServiceImpl", null, FirstBookServiceImpl.class)))
          .isInstanceOf(UnresolvableDependencyException.class)
          .hasMessageContaining(
              "Dependency 'firstBookServiceImpl' cannot be resolved. Element definition 'java.lang.String' "
                  + "is not assignable from 'ua.gorobeos.contextor.context.dependencies.SimpleDependencyResolverTest$FirstBookServiceImpl'."
          );
    }
  }

  private static List<AnnotationElementDefinition> getElementDefinitions() {
    return new ArrayList<>(List.of(AnnotationElementDefinition.builder()
            .name("bookController")
            .isPrimary(false)
            .type(BookController.class)
            .scope(AnnotationElementDefinition.SINGLETON_SCOPE)
            .dependencies(
                List.of(
                    DependencyDefinition.builder()
                        .name("bookService")
                        .clazz(BookService.class)
                        .build()
                )
            ).build(),
        AnnotationElementDefinition.builder()
            .name("firstBookServiceImpl")
            .isPrimary(false)
            .type(FirstBookServiceImpl.class)
            .scope(AnnotationElementDefinition.SINGLETON_SCOPE)
            .build(),
        AnnotationElementDefinition.builder()
            .name("secondBookServiceImpl")
            .isPrimary(false)
            .type(SecondBookServiceImpl.class)
            .scope(AnnotationElementDefinition.SINGLETON_SCOPE)
            .build(),
        AnnotationElementDefinition.builder()
            .isPrimary(false)
            .name("thirdBookServiceImpl")
            .type(ThirdBookServiceImpl.class)
            .scope(AnnotationElementDefinition.SINGLETON_SCOPE)
            .build()));
  }


  private static class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
      this.bookService = bookService;
    }
  }

  private interface BookService {

  }

  private static class FirstBookServiceImpl implements BookService {

  }

  private static class SecondBookServiceImpl implements BookService {

  }

  private static class ThirdBookServiceImpl implements BookService {

  }
}