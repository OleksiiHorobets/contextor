package ua.gorobeos.contextor.context.readers;

import static ua.gorobeos.contextor.context.element.ElementDefinition.SINGLETON_SCOPE;

import java.util.Collection;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ua.gorobeos.contextor.context.annotations.ContextConfig;
import ua.gorobeos.contextor.context.conditions.ConditionEvaluationUtils;
import ua.gorobeos.contextor.context.element.ConfigElementDefinition;
import ua.gorobeos.contextor.context.element.ElementDefinition;
import ua.gorobeos.contextor.context.readers.impl.ConfigElementDefinitionReader;
import ua.gorobeos.contextor.context.storage.ElementDefinitionHolder;
import ua.gorobeos.contextor.context.utils.ReflectionUtils;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ElementDefinitionReaderFacadeImpl implements ElementDefinitionReaderFacade {

  private enum DefinitionType {
    CONFIGURATION, ELEMENT
  }

  Map<DefinitionType, ElementDefinitionReader> elementDefinitionReader = Map.of(
      DefinitionType.CONFIGURATION, new ConfigElementDefinitionReader(),
      DefinitionType.ELEMENT, new AnnotatedBaseElementDefinitionReader()
  );
  ElementDefinitionHolder elementDefinitionHolder;

  @Override
  public void addElementDefinition(Class<?> clazz) {
    ElementDefinitionReader definitionReader = checkWhatReaderToUse(clazz);

    var elementDefinition = definitionReader.readElementDefinition(clazz);

    if (!checkIfElementConditionsArePassingOnlyIfSingleton(elementDefinition)) {
      return;
    }

    if (elementDefinition instanceof ConfigElementDefinition configDef) {
        configDef.getMethodDefinedElements()
            .stream()
            .filter(this::checkIfElementConditionsArePassingOnlyIfSingleton)
            .forEach(elementDefinitionHolder::addElementDefinition);
      }
    elementDefinitionHolder.addElementDefinition(elementDefinition);
  }

  private boolean checkIfElementConditionsArePassingOnlyIfSingleton(ElementDefinition elementDefinition) {
    if (!SINGLETON_SCOPE.equals(elementDefinition.getScope())) {
      log.debug("Element definition '{}' has scope '{}', so skipping conditional checks on definition registration", elementDefinition.getName(), elementDefinition.getScope());
      return true;
    }

    var context = ConditionEvaluationUtils.evaluate(elementDefinition.getType());
    if (!context.isConditionalCheckPassed()) {
      log.warn("Conditional check failed for element: {}. Scope is 'Singleton', so skipping saving element definition", elementDefinition.getName());
      log.warn("Conditional check results: {}", context.getConditionalCheckResults());
      return false;
    }
    return true;
  }

  private ElementDefinitionReader checkWhatReaderToUse(Class<?> clazz) {
    DefinitionType definitionType = ReflectionUtils.isAnnotationPresentFullCheck(clazz, ContextConfig.class)
        ? DefinitionType.CONFIGURATION
        : DefinitionType.ELEMENT;
    ElementDefinitionReader reader = elementDefinitionReader.get(definitionType);
    log.debug("Using {} reader for class: {}", reader.getClass().getSimpleName(), clazz.getName());
    return reader;
  }

  @Override
  public Collection<ElementDefinition> getElementDefinitions() {
    return elementDefinitionHolder.getElementDefinitions();
  }
}
