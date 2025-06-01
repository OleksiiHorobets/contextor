package ua.gorobeos.contextor.context.conditions.evaluators;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import ua.gorobeos.contextor.context.annotations.conditions.ConditionalOnWebRequest;
import ua.gorobeos.contextor.context.annotations.conditions.ConditionalOnWebRequest.HttpMethod;
import ua.gorobeos.contextor.context.conditions.ConditionalContext;
import ua.gorobeos.contextor.context.conditions.ConditionalEvaluator;
import ua.gorobeos.contextor.context.utils.ReflectionUtils;

@Slf4j
public class ConditionalOnWebRequestEvaluator implements ConditionalEvaluator {

  private final HttpClient httpClient;

  public ConditionalOnWebRequestEvaluator() {
    this.httpClient = HttpClient.newHttpClient();
  }

  @Override
  public ConditionalContext evaluate(ConditionalContext context) {
    if (!ReflectionUtils.isAnnotationPresentFullCheck(
        context.getElementClass(), ConditionalOnWebRequest.class)) {
      return context;
    }
    log.info("Evaluating ConditionalOnWebRequest for class: {}", context.getElementClass().getName());

    var httpMethod = ReflectionUtils.getValueFromAnnotation(
        context.getElementClass(), ConditionalOnWebRequest.class, "method", HttpMethod.class);
    var url = ReflectionUtils.getValueFromAnnotation(
        context.getElementClass(), ConditionalOnWebRequest.class, "url", String.class);

    if (httpMethod.isEmpty() || url.isEmpty()) {
      log.warn("ConditionalOnWebRequest evaluation failed: method or uri is null");
      context.setConditionalCheckPassed(false);
      context.getConditionalCheckResults().add(
          "ConditionalOnWebRequest evaluation failed: method or uri is null");
      return context;
    }

    //@SONAR_STOP@
    try {
      var httpRequest = httpClient.send(
              HttpRequest.newBuilder()
                  .uri(URI.create(url.get()))
                  .method(httpMethod.get().toString(), HttpRequest.BodyPublishers.noBody())
                  .build(),
              HttpResponse.BodyHandlers.ofString()
          );
      if (httpRequest.statusCode() >= 200 && httpRequest.statusCode() < 300) {
        return context;
      }
    } catch (IOException | InterruptedException e) {
      log.error("ConditionalOnWebRequest unexpectedly evaluation failed", e.getMessage(), e);
      context.setConditionalCheckPassed(false);
      context.getConditionalCheckResults().add(
          String.format("ConditionalOnWebRequest unexpected evaluation failed: %s", e.getMessage()));
    }
    //@SONAR_START@
    return context;
  }
}
