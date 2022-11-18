package com.project.sharedfolderserver.v1.utils.validation.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.sharedfolderserver.v1.file.exception.FileValidationError;
import com.project.sharedfolderserver.v1.utils.json.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
/**
 * Interceptor to intercept controller method using @Validate on a parameter
 * It pass it in Validator service and throws exception if the object schemas doesn't match
 */
public class ValidatedRequestBodyInterceptor implements HandlerMethodArgumentResolver {

    private final ValidationService validationService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Validate.class);

    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        log.debug("Starting validate");
        String jsonSchemaPath = Objects.requireNonNull(parameter.getParameterAnnotation(Validate.class)).value();
        log.debug("Json Schema path: {}", jsonSchemaPath);
        JsonNode jsonBody = getJsonPayload(webRequest);
        validationService.validate(jsonBody, jsonSchemaPath);
        Class<?> objectType = parameter.getParameterType();
        log.debug("Success validate object [{}]", objectType);
        return JsonUtil.objectMapper.treeToValue(jsonBody, objectType);
    }

    private JsonNode getJsonPayload(NativeWebRequest nativeWebRequest) throws IOException {
        if (nativeWebRequest == null) {
            log.error("Native request is null, can't validate");
            throw new FileValidationError("Request is null, can't validate");
        }
        HttpServletRequest httpServletRequest = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        if (httpServletRequest == null) {
            log.error("Http servlet request is null, can't validate");
            throw new FileValidationError("Request is null, can't validate");
        }
        return JsonUtil.objectMapper.readTree(httpServletRequest.getInputStream());

    }
}
