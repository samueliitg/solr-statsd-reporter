package com.myntra.solr.statsd.reporter;

import com.codahale.metrics.MetricFilter;
import com.readytalk.metrics.StatsDReporter;
import org.apache.solr.metrics.FilteringSolrMetricReporter;
import org.apache.solr.metrics.SolrMetricManager;
import org.apache.solr.metrics.reporters.ReporterClientCache;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Samuel Tatipamula. Written on 09/11/17.
 **/

public class SolrStatsDReporter extends FilteringSolrMetricReporter {

    private String host = null;
    private int port = -1;
    private String instancePrefix = null;
    private StatsDReporter reporter = null;

    private static final ReporterClientCache<StatsDReporter> serviceRegistry = new ReporterClientCache<>();

    public SolrStatsDReporter(SolrMetricManager metricManager, String registryName) {
        super(metricManager, registryName);
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setPrefix(String prefix) {
        this.instancePrefix = prefix;
    }

    /**
     * Reporter initialization implementation.
     */
    protected void doInit() {
        if (reporter != null) {
            throw new IllegalStateException("Already started once?");
        }

        if (instancePrefix == null) {
            instancePrefix = registryName;
        } else {
            instancePrefix = instancePrefix + "." + registryName;
        }
        final MetricFilter filter = newMetricFilter();
        reporter = StatsDReporter.forRegistry(metricManager.registry(registryName))
                .prefixedWith(instancePrefix)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS).filter(filter).build(host, port);
        reporter.start(period, TimeUnit.SECONDS);
    }

    /**
     * Validates that the reporter has been correctly configured.
     *
     * @throws IllegalStateException if the reporter is not properly configured
     */
    protected void validate() throws IllegalStateException {
        if (host == null) {
            throw new IllegalStateException("Init argument 'host' must be set to a valid StatsD server name.");
        }
        if (port == -1) {
            throw new IllegalStateException("Init argument 'port' must be set to a valid StatsD server port, like 8125");
        }
        if (period < 1) {
            throw new IllegalStateException("Init argument 'period' is in time unit 'seconds' and must be at least 1.");
        }
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     * <p>
     * <p> As noted in AutoCloseable#close, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     * @throws IOException if an I/O error occurs
     */
    public void close() throws IOException {
        if (reporter != null) {
            reporter.close();
        }
    }
}
