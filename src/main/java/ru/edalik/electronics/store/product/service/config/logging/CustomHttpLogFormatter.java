package ru.edalik.electronics.store.product.service.config.logging;

import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Precorrelation;

import java.util.concurrent.TimeUnit;

public class CustomHttpLogFormatter implements HttpLogFormatter {

    @Override
    public String format(Precorrelation precorrelation, HttpRequest request) {
        return "Request: %s %s".formatted(request.getMethod(), request.getRequestUri());
    }

    @Override
    public String format(Correlation correlation, HttpResponse response) {
        return "Response: %s (Duration: %s ms)".formatted(
            response.getStatus(),
            TimeUnit.NANOSECONDS.toMillis(correlation.getDuration().getNano())
        );
    }

}