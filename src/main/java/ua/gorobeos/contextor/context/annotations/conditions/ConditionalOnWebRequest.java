package ua.gorobeos.contextor.context.annotations.conditions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConditionalOnWebRequest {

  HttpMethod method() default HttpMethod.GET;
  String url();

  enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH
  }
}
