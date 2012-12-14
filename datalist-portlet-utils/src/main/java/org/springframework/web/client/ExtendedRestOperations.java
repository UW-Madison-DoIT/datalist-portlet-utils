package org.springframework.web.client;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

/**
 * Adds some utility methods to {@link RestOperations}
 * 
 * @author Eric Dalquist
 */
public interface ExtendedRestOperations extends RestOperations {

    /**
     * Retrieve a representation by doing a GET on the specified URL.
     * The response (if any) is converted and returned.
     * <p>URI Template variables are expanded using the given URI variables, if any.
     * @param url the URL
     * @param headers headers to set on the request
     * @param responseType the type of the return value
     * @param uriVariables the variables to expand the template
     * @return the converted object
     * 
     * @see RestOperations#getForObject(String, Class, Object...)
     */
    <T> T getForObject(String url, Class<T> responseType, HttpHeaders headers, Object... uriVariables)
            throws RestClientException;
    
    /**
     * Proxies the response from a REST request, writing the status, headers and body to the {@link ProxyResponse}
     * 
     * @param proxyResponse response to write the results of the request to
     * @param url the URL
     * @param headers header to set on the request
     * @param uriVariables the variables to expand the template
     * @return The number of bytes copied in the body
     */
    int proxyRequest(ProxyResponse proxyResponse, String url, HttpMethod method, HttpHeaders headers, Object... uriVariables)
            throws RestClientException;
    
    /**
     * Response handler for proxying a rest request.
     */
    public static interface ProxyResponse {
        /**
         * @param status The {@link HttpStatus} returned by the original request
         */
        void setHttpStatus(HttpStatus status);

        /**
         * @param headers The {@link HttpHeaders} returned by the original request
         */
        void setHttpHeaders(HttpHeaders headers);
        
        /**
         * @return {@link OutputStream} to write the body of the original request to
         */
        OutputStream getOutputStream() throws IOException;
    }
}