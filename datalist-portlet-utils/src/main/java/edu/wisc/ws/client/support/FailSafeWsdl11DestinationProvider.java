/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.ws.client.support;

import org.springframework.core.io.Resource;
import org.springframework.ws.client.support.destination.Wsdl11DestinationProvider;

/**
 * Wraps the WSDL {@link Resource} so that {@link Resource#exists()} always returns true.
 * 
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
public class FailSafeWsdl11DestinationProvider extends Wsdl11DestinationProvider {

    @Override
    public void setWsdl(Resource wsdlResource) {
        super.setWsdl(new DelegatingResource(wsdlResource) {
            @Override
            public boolean exists() {
                return true;
            }
        });
    }
    
}
