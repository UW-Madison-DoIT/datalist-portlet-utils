package edu.wisc.commons.httpclient;

import java.util.Collections;
import java.util.Set;

import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * Utility for creating a {@link SchemeRegistry} in spring
 * 
 * @author Eric Dalquist
 */
public class SchemeRegistryFactoryBean extends AbstractFactoryBean<SchemeRegistry> {
    private boolean extendDefault = true;
    private Set<Scheme> schemes = Collections.emptySet();
    
    /**
     * If true {@link SchemeRegistryFactory#createDefault()} is used to create the {@link SchemeRegistry}
     * before the additional schemes are registered via {@link SchemeRegistry#register(Scheme)}, defaults
     * to true; 
     */
    public void setExtendDefault(boolean extendDefault) {
        this.extendDefault = extendDefault;
    }

    /**
     * {@link Scheme}s to register with the {@link SchemeRegistry}
     */
    public void setSchemes(Set<Scheme> schemes) {
        this.schemes = schemes;
    }

    @Override
    public Class<?> getObjectType() {
        return SchemeRegistry.class;
    }

    @Override
    protected SchemeRegistry createInstance() throws Exception {
        //Create the registry
        final SchemeRegistry registry;
        if (extendDefault) {
            registry = SchemeRegistryFactory.createDefault();
        }
        else {
            registry = new SchemeRegistry();
        }
        
        //Register additional schemes
        for (final Scheme scheme : schemes) {
            registry.register(scheme);
        }
        
        return registry;
    }

}
