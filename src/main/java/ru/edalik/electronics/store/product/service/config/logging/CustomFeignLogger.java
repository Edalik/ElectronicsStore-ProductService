package ru.edalik.electronics.store.product.service.config.logging;

import feign.Logger;
import feign.Request;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.io.IOException;

@Slf4j
public class CustomFeignLogger extends Logger {

    private static final String REQUEST_LOG = "Feign client request: {}";
    private static final String REQUEST_URL = "request.url";
    private static final String REQUEST_METHOD = "request.method";
    private static final String REQUEST_HEADERS = "request.headers";
    private static final String REQUEST_BODY = "request.body";
    private static final String RESPONSE_LOG = "Feign client response: {}";
    private static final String RESPONSE_HEADERS = "response.headers";
    private static final String RESPONSE_BODY = "response.body";
    private static final String RESPONSE_STATUS = "response.status";

    @Override
    protected void log(String configKey, String format, Object... args) {
        // not implemented because: spamming logs
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        byte[] reqBody = request.body() != null ? request.body() : new byte[0];
        String url = "%s %s".formatted(request.httpMethod(), request.url());

        MDC.put(REQUEST_URL, url);
        MDC.put(REQUEST_METHOD, request.httpMethod().name());
        MDC.put(REQUEST_BODY, new String(reqBody));
        MDC.put(REQUEST_HEADERS, request.headers().toString());

        log.info(REQUEST_LOG, url);

        MDC.remove(REQUEST_URL);
        MDC.remove(REQUEST_BODY);
        MDC.remove(REQUEST_HEADERS);
    }

    @Override
    protected Response logAndRebufferResponse(
        String configKey,
        Level logLevel,
        Response response,
        long elapsedTime
    ) throws IOException {
        byte[] resBody = response.body() != null ? response.body().asInputStream().readAllBytes() : new byte[0];

        logResponse(response, resBody);

        return response.toBuilder()
            .body(resBody)
            .build();
    }

    protected void logResponse(Response response, byte[] resBody) {
        String url = "%s %s".formatted(response.request().httpMethod(), response.request().url());
        MDC.put(REQUEST_URL, url);
        MDC.put(RESPONSE_HEADERS, response.headers().toString());
        MDC.put(RESPONSE_BODY, new String(resBody));
        MDC.put(RESPONSE_STATUS, String.valueOf(response.status()));

        log.info(RESPONSE_LOG, url);

        MDC.remove(REQUEST_URL);
        MDC.remove(RESPONSE_HEADERS);
        MDC.remove(RESPONSE_BODY);
        MDC.remove(RESPONSE_STATUS);
    }

}