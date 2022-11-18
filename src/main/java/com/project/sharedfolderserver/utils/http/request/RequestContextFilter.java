package com.project.sharedfolderserver.utils.http.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
/**
 * Filter to http request
 * Logging incoming request
 * Add request id to context
 * Add request id header to response
 */
public class RequestContextFilter implements Filter {
    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    private final Context context;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        Map<String, String> headers = extractHeadersFromRequest(httpServletRequest);
        log.info("Incoming request: {} to {}, headers: [{}]", httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), headers);
        String requestIdHeader = httpServletRequest.getHeader(REQUEST_ID_HEADER);
        if (StringUtils.isNotEmpty(requestIdHeader)) {
            context.setRequestId(requestIdHeader);
        } else {
            context.setRequestId(UUID.randomUUID().toString());
        }
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        httpServletResponse.setHeader(REQUEST_ID_HEADER, context.getRequestId());
        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * Extract headers from http request
     * @param httpServletRequest - http servlet request
     * @return - map of headers and values
     */
    private Map<String, String> extractHeadersFromRequest(HttpServletRequest httpServletRequest) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> enumerationHeaders = httpServletRequest.getHeaderNames();
        while (enumerationHeaders.hasMoreElements()) {
            String headerName = enumerationHeaders.nextElement();
            headers.put(headerName, httpServletRequest.getHeader(headerName));
        }
        return headers;
    }
}
