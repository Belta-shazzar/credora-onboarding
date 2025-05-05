package com.credora.onboarding.common.response_interceptor;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class GlobalResponseInterceptor implements ResponseBodyAdvice<Object> {
  @Autowired
  private HttpServletResponse servletResponse;

  @Override
  public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
    return true;
  }

  @Override
  public Object beforeBodyWrite(
          Object body,
          MethodParameter returnType,
          MediaType selectedContentType,
          Class<? extends HttpMessageConverter<?>> selectedConverterType,
          ServerHttpRequest request,
          ServerHttpResponse response
  ) {

    if (body instanceof ApiResponse) { // || body instanceof ResponseEntity) {
      return body;
    }

    int status = servletResponse.getStatus();

    boolean success = (status >= 200 && status < 300);

    return new ApiResponse<>(success, body);
  }
}
