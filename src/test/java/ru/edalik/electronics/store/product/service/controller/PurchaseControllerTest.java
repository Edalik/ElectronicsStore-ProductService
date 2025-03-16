package ru.edalik.electronics.store.product.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.edalik.electronics.store.product.service.mapper.PurchaseMapper;
import ru.edalik.electronics.store.product.service.model.dto.PurchaseDto;
import ru.edalik.electronics.store.product.service.model.entity.Purchase;
import ru.edalik.electronics.store.product.service.model.exception.InsufficientFunds;
import ru.edalik.electronics.store.product.service.model.exception.InsufficientQuantityException;
import ru.edalik.electronics.store.product.service.model.exception.NotFoundException;
import ru.edalik.electronics.store.product.service.service.interfaces.PurchaseService;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PurchaseController.class)
class PurchaseControllerTest {

    static final String BASE_URL = "/api/v1/products/purchases";
    static final String USER_ID_HEADER = "User-Id";
    static final UUID USER_ID = UUID.randomUUID();
    static final UUID PURCHASE_ID = UUID.randomUUID();
    static final String PRODUCT_ID_PARAM = "product-id";
    static final UUID PRODUCT_ID = UUID.randomUUID();
    static final String QUANTITY_PARAM = "quantity";
    static final int QUANTITY = 2;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    PurchaseService purchaseService;

    @MockitoBean
    PurchaseMapper purchaseMapper;

    @Test
    @SneakyThrows
    void makePurchase_ValidRequest_ReturnsPurchaseDto() {
        Purchase purchase = mock(Purchase.class);
        PurchaseDto dto = mock(PurchaseDto.class);

        when(purchaseService.makePurchase(USER_ID, PRODUCT_ID, QUANTITY)).thenReturn(purchase);
        when(purchaseMapper.toDto(purchase)).thenReturn(dto);

        mockMvc.perform(
                post(BASE_URL)
                    .header(USER_ID_HEADER, USER_ID)
                    .param(PRODUCT_ID_PARAM, PRODUCT_ID.toString())
                    .param(QUANTITY_PARAM, String.valueOf(QUANTITY))
            )
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    @SneakyThrows
    void makePurchase_InvalidQuantity_ReturnsValidationError() {
        mockMvc.perform(
                post(BASE_URL)
                    .header(USER_ID_HEADER, USER_ID)
                    .param(PRODUCT_ID_PARAM, PRODUCT_ID.toString())
                    .param(QUANTITY_PARAM, "-1")
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fields[0].field").value(QUANTITY_PARAM));
    }

    @Test
    @SneakyThrows
    void makePurchase_ProductNotFound_ReturnsNotFound() {
        String errorMessage = "Product not found";
        when(purchaseService.makePurchase(USER_ID, PRODUCT_ID, QUANTITY))
            .thenThrow(new NotFoundException(errorMessage));

        mockMvc.perform(
                post(BASE_URL)
                    .header(USER_ID_HEADER, USER_ID)
                    .param(PRODUCT_ID_PARAM, PRODUCT_ID.toString())
                    .param(QUANTITY_PARAM, String.valueOf(QUANTITY))
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    @SneakyThrows
    void makePurchase_InsufficientQuantity_ReturnsBadRequest() {
        when(purchaseService.makePurchase(USER_ID, PRODUCT_ID, QUANTITY))
            .thenThrow(new InsufficientQuantityException());

        mockMvc.perform(
                post(BASE_URL)
                    .header(USER_ID_HEADER, USER_ID)
                    .param(PRODUCT_ID_PARAM, PRODUCT_ID.toString())
                    .param(QUANTITY_PARAM, String.valueOf(QUANTITY))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Insufficient product quantity"));
    }

    @Test
    @SneakyThrows
    void makePurchase_InsufficientFunds_ReturnsBadRequest() {
        when(purchaseService.makePurchase(USER_ID, PRODUCT_ID, QUANTITY))
            .thenThrow(new InsufficientFunds());

        mockMvc.perform(
                post(BASE_URL)
                    .header(USER_ID_HEADER, USER_ID)
                    .param(PRODUCT_ID_PARAM, PRODUCT_ID.toString())
                    .param(QUANTITY_PARAM, String.valueOf(QUANTITY))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Insufficient funds"));
    }

    @Test
    @SneakyThrows
    void getPurchaseById_ValidRequest_ReturnsPurchaseDto() {
        Purchase purchase = mock(Purchase.class);
        PurchaseDto dto = mock(PurchaseDto.class);

        when(purchaseService.getPurchaseById(USER_ID, PURCHASE_ID)).thenReturn(purchase);
        when(purchaseMapper.toDto(purchase)).thenReturn(dto);

        mockMvc.perform(
                get(BASE_URL + "/{id}", PURCHASE_ID)
                    .header(USER_ID_HEADER, USER_ID)
            )
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    @SneakyThrows
    void getPurchaseById_NotFound_ReturnsNotFound() {
        String errorMessage = "Purchase not found";
        when(purchaseService.getPurchaseById(USER_ID, PURCHASE_ID))
            .thenThrow(new NotFoundException(errorMessage));

        mockMvc.perform(
                get(BASE_URL + "/{id}", PURCHASE_ID)
                    .header(USER_ID_HEADER, USER_ID)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    @SneakyThrows
    void getPurchases_ValidRequest_ReturnsPurchaseList() {
        List<Purchase> purchases = List.of(mock(Purchase.class));
        List<PurchaseDto> dtos = List.of(mock(PurchaseDto.class));

        when(purchaseService.getPurchases(USER_ID)).thenReturn(purchases);
        when(purchaseMapper.toDto(purchases)).thenReturn(dtos);

        mockMvc.perform(
                get(BASE_URL)
                    .header(USER_ID_HEADER, USER_ID)
            )
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(dtos)));
    }

    @Test
    @SneakyThrows
    void makePurchase_MissingUserId_ReturnsBadRequest() {
        mockMvc.perform(
                post(BASE_URL)
                    .param(PRODUCT_ID_PARAM, PRODUCT_ID.toString())
                    .param(QUANTITY_PARAM, "1")
            )
            .andExpect(status().isBadRequest());
    }

}