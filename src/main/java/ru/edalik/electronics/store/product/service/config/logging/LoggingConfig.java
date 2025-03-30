package ru.edalik.electronics.store.product.service.config.logging;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.core.DefaultHttpLogWriter;
import org.zalando.logbook.core.DefaultSink;

@Configuration
public class LoggingConfig {

    @Bean
    public Logbook logbook() {
        DefaultSink defaultSink = new DefaultSink(new CustomHttpLogFormatter(), new DefaultHttpLogWriter());

        return Logbook.builder()
            .sink(new CustomLogbookSink(defaultSink))
            .build();
    }

    @Bean
    public AbstractRequestLoggingFilter logFilter() {
        AbstractRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(100000);
        filter.setIncludeHeaders(true);
        return filter;
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    public Logger feignLogger() {
        return new CustomFeignLogger();
    }

}