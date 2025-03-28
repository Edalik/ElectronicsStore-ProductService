package ru.edalik.electronics.store.product.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.edalik.electronics.store.product.service.config.FeignClientTokenConfig;
import ru.edalik.electronics.store.product.service.model.dto.BalanceDto;

@FeignClient(
    value = "user-service-client",
    url = "${custom.feign.client.user.service.url}",
    configuration = FeignClientTokenConfig.class
)
public interface UserServiceClient {

    @PostMapping("/balance/payment")
    ResponseEntity<Void> payment(@RequestBody BalanceDto dto);

}