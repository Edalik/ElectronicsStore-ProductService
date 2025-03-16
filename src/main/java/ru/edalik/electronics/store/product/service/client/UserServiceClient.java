package ru.edalik.electronics.store.product.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.edalik.electronics.store.product.service.model.dto.BalanceDto;

import java.util.UUID;

@FeignClient(
    value = "user-service-client",
    url = "${custom.feign.client.user.service.url}"
)
public interface UserServiceClient {

    @PostMapping("/balance/payment")
    ResponseEntity<Void> payment(@RequestBody BalanceDto dto, @RequestHeader("User-Id") UUID userId);

}