/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package edu.wisc.web.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/**
 * Maps view names to status codes
 * 
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
public class StatusMappingExceptionResolver extends SimpleMappingExceptionResolver {
    private Map<String, Integer> viewStatusCodeMappings = Collections.emptyMap();
    
    public void setViewStatusCodeMappings(Map<String, Integer> viewStatusCodeMappings) {
        this.viewStatusCodeMappings = new LinkedHashMap<String, Integer>(viewStatusCodeMappings);
    }

    @Override
    protected Integer determineStatusCode(HttpServletRequest request, String viewName) {
        final Integer statusCode = this.viewStatusCodeMappings.get(viewName);
        if (statusCode != null) {
            return statusCode;
        }
        
        return super.determineStatusCode(request, viewName);
    }

}
