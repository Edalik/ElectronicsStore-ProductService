package ru.edalik.electronics.store.product.service.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import ru.edalik.electronics.store.product.service.service.security.UserContextService;

@RequiredArgsConstructor
public class FeignClientTokenConfig {

    private final UserContextService userContextService;

    @Bean
    public RequestInterceptor contractRequestInterceptor() {
        return requestTemplate -> requestTemplate
            .header(
                "Authorization",
                String.format("%s %s", "Bearer", userContextService.getTokenString())
            );
    }

}