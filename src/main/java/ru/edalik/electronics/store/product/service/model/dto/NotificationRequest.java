package ru.edalik.electronics.store.product.service.model.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record NotificationRequest(
    UUID userId,
    String text
) {

}