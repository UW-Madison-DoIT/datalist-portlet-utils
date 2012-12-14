package org.springframework.web.client;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.ExtendedRestOperations.ProxyResponse;

import com.google.common.collect.ImmutableSet;

/**
 * Implementation of {@link ProxyResponse} that wraps a {@link HttpServletResponse}
 * 
 * @author Eric Dalquist
 */
public class HttpServletProxyResponse implements ProxyResponse {
    private final HttpServletResponse servletResponse;
    private final Set<String> excludedHeaders;

    public HttpServletProxyResponse(HttpServletResponse servletResponse) {
        this.servletResponse = servletResponse;
        this.excludedHeaders = Collections.emptySet();
    }
    
    public HttpServletProxyResponse(HttpServletResponse servletResponse, Set<String> excludedHeaders) {
        this.servletResponse = servletResponse;
        this.excludedHeaders = ImmutableSet.copyOf(excludedHeaders);
    }

    @Override
    public void setHttpStatus(HttpStatus status) {
        this.servletResponse.setStatus(status.value());
    }

    @Override
    public void setHttpHeaders(HttpHeaders headers) {
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String headerName = entry.getKey();
            if (!this.excludedHeaders.contains(headerName)) {
                for (String headerValue : entry.getValue()) {
                    this.servletResponse.addHeader(headerName, headerValue);
                }
            }
        }        
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return servletResponse.getOutputStream();
    }
}
