package com.project.sharedfolderserver.utils.http.response;

import com.project.sharedfolderserver.v1.utils.http.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Slf4j
@ControllerAdvice
/**
 *  Http response controller advice
 *  Intercept annotated class with @ResponseWrapper and wrap the returned object with a Response.class object
 */
public class ResponseInterceptor implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        log.trace("body: {}" ,body);
        if (!returnType.getDeclaringClass().isAnnotationPresent(RestController.class) || !returnType.getDeclaringClass().isAnnotationPresent(ResponseWrapper.class) || body instanceof Response)
            return body;
        return new Response(body);
    }
}
