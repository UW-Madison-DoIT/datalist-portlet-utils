package edu.wisc.commons.httpclient;

import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.pool.PoolStats;

/**
 * Defines statistics exposed by {@link PoolingClientConnectionManager}
 * 
 */
public interface ConnPoolControlDataMXBean {

    void setMaxTotal(int max);

    int getMaxTotal();

    void setDefaultMaxPerRoute(int max);

    int getDefaultMaxPerRoute();

    PoolStats getTotalStats();

    int getLeased();

    int getPending();

    int getAvailable();

    int getMax();

}