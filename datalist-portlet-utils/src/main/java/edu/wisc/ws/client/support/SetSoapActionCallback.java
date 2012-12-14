/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.ws.client.support;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

/**
 * Calls {@link SaajSoapMessage#setSoapAction(String)} with the configured String if the {@link WebServiceMessage} is an
 * instance of {@link SaajSoapMessage}
 * 
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
public class SetSoapActionCallback implements WebServiceMessageCallback {
    private final String soapAction;
    
    public SetSoapActionCallback(String soapAction) {
        this.soapAction = soapAction;
    }
    
    public String getSoapAction() {
        return soapAction;
    }

    @Override
    public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
        if (message instanceof SaajSoapMessage) {
            final SaajSoapMessage casted = (SaajSoapMessage) message;
            casted.setSoapAction(this.soapAction);
        }
    }
}