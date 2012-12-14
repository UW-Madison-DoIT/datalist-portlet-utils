package edu.wisc.http.converter.xml;

import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.xml.AbstractXmlHttpMessageConverter;
import org.springframework.util.Assert;

/**
 * XML message converter that uses a {@link JAXBContext} directly. The Class specified to {@link #readFromSource(Class, HttpHeaders, Source)}
 * is passed to the {@link Unmarshaller#unmarshal(Source, Class)} method to help with parsing of XML that doesn't include namespace information.
 * 
 * Note that {@link #supports(Class)} returns true for ALL Classes so this converter should only be used in a limited scope
 * 
 * @author Eric Dalquist
 */
public class JaxbMarshallingHttpMessageConverter extends AbstractXmlHttpMessageConverter<Object> {

    private JAXBContext jaxbContext;

    public JaxbMarshallingHttpMessageConverter(JAXBContext jaxbContext) {
        Assert.notNull(jaxbContext, "JAXBContext must not be null");
        this.jaxbContext = jaxbContext;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    protected Object readFromSource(Class<?> clazz, HttpHeaders headers, Source source) throws IOException {
        try {
            final Unmarshaller unmarshaller = this.jaxbContext.createUnmarshaller();
            
            Object result = unmarshaller.unmarshal(source, clazz);
            if (result instanceof JAXBElement<?>) {
                result = ((JAXBElement<?>) result).getValue();
            }
            
            if (!clazz.isInstance(result)) {
                throw new TypeMismatchException(result, clazz);
            }
            return result;
        }
        catch (JAXBException e) {
            throw new HttpMessageNotReadableException("Could not read [" + clazz + "]", e);
        }
    }

    @Override
    protected void writeToResult(Object o, HttpHeaders headers, Result result) throws IOException {
        try {
            final Marshaller marshaller = this.jaxbContext.createMarshaller();
            marshaller.marshal(o, result);
        }
        catch (JAXBException e) {
            throw new HttpMessageNotWritableException("Could not write [" + o + "]", e);
        }
    }
}
