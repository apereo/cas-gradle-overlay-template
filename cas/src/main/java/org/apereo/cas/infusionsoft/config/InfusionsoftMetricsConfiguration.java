package org.apereo.cas.infusionsoft.config;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(InfusionsoftConfigurationProperties.class)
public class InfusionsoftMetricsConfiguration {

    @Autowired
    private InfusionsoftConfigurationProperties infusionsoftProperties;

    @Autowired
    @Qualifier("metrics")
    private MetricRegistry metricRegistry;

    @Bean
    @ConditionalOnProperty("infusionsoft.graphite.enabled")
    public GraphiteReporter graphiteReporter() {
        final Graphite graphite = new Graphite(new InetSocketAddress(infusionsoftProperties.getGraphite().getDomain(), infusionsoftProperties.getGraphite().getPort()));
        final GraphiteReporter reporter = GraphiteReporter.forRegistry(metricRegistry)
                .prefixedWith(infusionsoftProperties.getGraphite().getPrefix())
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .build(graphite);
        reporter.start(1, TimeUnit.MINUTES);

        return reporter;
    }

}
