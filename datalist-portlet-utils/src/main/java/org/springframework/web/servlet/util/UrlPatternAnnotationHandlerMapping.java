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
package org.springframework.web.servlet.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping;

/**
 * Extension of DefaultAnnotationHandlerMapping which only registered handlers for urls that match
 * an include rule AND do not match any exclude rule. 
 * 
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
public class UrlPatternAnnotationHandlerMapping extends DefaultAnnotationHandlerMapping {
    private Set<Pattern> includedUrls = Collections.singleton(Pattern.compile(".*"));
    private Set<Pattern> excludedUrls = Collections.emptySet();
    
    public void setIncludedUrls(Set<String> includedUrls) {
        this.includedUrls = this.compilePatterns(includedUrls);
    }

    public void setExcludedUrls(Set<String> excludedUrls) {
        this.excludedUrls = this.compilePatterns(excludedUrls);
    }
    
    protected Set<Pattern> compilePatterns(Set<String> urls) {
        final Set<Pattern> urlPatterns = new LinkedHashSet<Pattern>(urls.size());
        
        for (final String includedUrl : urls) {
            urlPatterns.add(Pattern.compile(includedUrl));
        }
        
        return urlPatterns;
    }

    @Override
    protected String[] determineUrlsForHandler(String beanName) {
        final String[] urls = super.determineUrlsForHandler(beanName);
        if (urls == null || urls.length == 0) {
            return urls;
        }
        
        final List<String> filteredUrls = new ArrayList<String>(urls.length);
        
        for (final String url : urls) {
            if (this.included(url) && !this.excluded(url)) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Including url: " + url);
                }
                filteredUrls.add(url);
            }
            else if (this.logger.isDebugEnabled()) {
                this.logger.debug("Excluding url: " + url);
            }
        }
        
        return filteredUrls.toArray(new String[filteredUrls.size()]);
    }

    /**
     * Check if the URL matches one of the configured include patterns
     */
    protected boolean included(String url) {
        for (final Pattern urlPattern : this.includedUrls) {
            final Matcher matcher = urlPattern.matcher(url);
            if (matcher.matches()) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Include pattern " + urlPattern.pattern() + " matches " + url);
                }
                return true;
            }
        }
        
        return false;
    }

    /**
     * Check if the URL matches one of the configured exclude patterns
     */
    protected boolean excluded(String url) {
        for (final Pattern urlPattern : this.excludedUrls) {
            final Matcher matcher = urlPattern.matcher(url);
            if (matcher.matches()) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Exclude pattern " + urlPattern.pattern() + " matches " + url);
                }
                return true;
            }
        }
        
        return false;
    }
}
