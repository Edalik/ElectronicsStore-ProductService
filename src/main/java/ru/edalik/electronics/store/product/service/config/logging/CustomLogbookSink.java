package ru.edalik.electronics.store.product.service.config.logging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Precorrelation;
import org.zalando.logbook.Sink;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class CustomLogbookSink implements Sink {

    private static final String REQUEST_URL = "request.url";
    private static final String REQUEST_METHOD = "request.method";
    private static final String REQUEST_HEADERS = "request.headers";
    private static final String REQUEST_BODY = "request.body";
    private static final String REQUEST_CLIENT_IP = "request.clientIp";
    private static final String RESPONSE_HEADERS = "response.headers";
    private static final String RESPONSE_BODY = "response.body";
    private static final String RESPONSE_STATUS = "response.status";

    private final Sink delegate;

    @Override
    public void write(Precorrelation precorrelation, HttpRequest request) throws IOException {
        try {
            MDC.put(REQUEST_URL, request.getRequestUri());
            MDC.put(REQUEST_METHOD, request.getMethod());
            MDC.put(REQUEST_HEADERS, request.getHeaders().toString());
            MDC.put(REQUEST_BODY, request.getBodyAsString());
            MDC.put(REQUEST_CLIENT_IP, request.getRemote());

            delegate.write(precorrelation, request);
        } finally {
            MDC.remove(REQUEST_URL);
            MDC.remove(REQUEST_METHOD);
            MDC.remove(REQUEST_HEADERS);
            MDC.remove(REQUEST_BODY);
            MDC.remove(REQUEST_CLIENT_IP);
        }
    }

    @Override
    public void write(Correlation correlation, HttpRequest request, HttpResponse response) throws IOException {
        try {
            MDC.put(RESPONSE_STATUS, String.valueOf(response.getStatus()));
            MDC.put(RESPONSE_HEADERS, response.getHeaders().toString());
            MDC.put(RESPONSE_BODY, response.getBodyAsString());

            delegate.write(correlation, request, response);
        } finally {
            MDC.remove(RESPONSE_STATUS);
            MDC.remove(RESPONSE_HEADERS);
            MDC.remove(RESPONSE_BODY);
        }
    }

}