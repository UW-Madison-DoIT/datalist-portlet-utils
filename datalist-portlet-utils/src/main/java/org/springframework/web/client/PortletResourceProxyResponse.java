package org.springframework.web.client;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ResourceResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.ExtendedRestOperations.ProxyResponse;

import com.google.common.collect.ImmutableSet;

/**
 * Implementation of {@link ProxyResponse} that wraps a portlet {@link ResourceResponse}
 * 
 * @author Eric Dalquist
 */
public class PortletResourceProxyResponse implements ProxyResponse {
    private final ResourceResponse resourceResponse;
    private final Set<String> excludedHeaders;

    public PortletResourceProxyResponse(ResourceResponse resourceResponse) {
        this.resourceResponse = resourceResponse;
        this.excludedHeaders = Collections.emptySet();
    }
    
    public PortletResourceProxyResponse(ResourceResponse resourceResponse, Set<String> excludedHeaders) {
        this.resourceResponse = resourceResponse;
        this.excludedHeaders = ImmutableSet.copyOf(excludedHeaders);
    }

    @Override
    public void setHttpStatus(HttpStatus status) {
        this.resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE, Integer.toString(status.value()));
    }

    @Override
    public void setHttpHeaders(HttpHeaders headers) {
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String headerName = entry.getKey();
            if (!this.excludedHeaders.contains(headerName)) {
                for (String headerValue : entry.getValue()) {
                    this.resourceResponse.addProperty(headerName, headerValue);
                }
            }
        }        
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return resourceResponse.getPortletOutputStream();
    }
}
