package ru.edalik.electronics.store.product.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.edalik.electronics.store.product.service.mapper.BasketMapper;
import ru.edalik.electronics.store.product.service.model.dto.BasketDto;
import ru.edalik.electronics.store.product.service.model.entity.Basket;
import ru.edalik.electronics.store.product.service.model.exception.InsufficientQuantityException;
import ru.edalik.electronics.store.product.service.model.exception.NotFoundException;
import ru.edalik.electronics.store.product.service.service.interfaces.BasketService;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BasketController.class)
class BasketControllerTest {

    static final String BASE_URL = "/api/v1/products/basket";
    static final String USER_ID_HEADER = "User-Id";
    static final UUID USER_ID = UUID.randomUUID();
    static final String PRODUCT_ID_PARAM = "product-id";
    static final UUID PRODUCT_ID = UUID.randomUUID();
    static final String QUANTITY_PARAM = "quantity";
    static final int QUANTITY = 2;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    BasketService basketService;

    @MockitoBean
    BasketMapper basketMapper;

    @Test
    @SneakyThrows
    void addToBasket_ValidRequest_ReturnsBasketDto() {
        Basket basket = mock(Basket.class);
        BasketDto dto = mock(BasketDto.class);

        when(basketService.addToBasket(USER_ID, PRODUCT_ID, QUANTITY)).thenReturn(basket);
        when(basketMapper.toDto(basket)).thenReturn(dto);

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
    void addToBasket_InvalidQuantity_ReturnsValidationError() {
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
    void addToBasket_ExceedingQuantity_ReturnsValidationError() {
        when(basketService.addToBasket(USER_ID, PRODUCT_ID, QUANTITY)).thenThrow(new InsufficientQuantityException());

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
    void addToBasket_ProductNotFound_ReturnsNotFound() {
        String errorMessage = "Product not found";
        int quantity = 1;
        when(basketService.addToBasket(USER_ID, PRODUCT_ID, quantity))
            .thenThrow(new NotFoundException(errorMessage));

        mockMvc.perform(post(BASE_URL)
                .header(USER_ID_HEADER, USER_ID)
                .param(PRODUCT_ID_PARAM, PRODUCT_ID.toString())
                .param(QUANTITY_PARAM, String.valueOf(quantity)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    @SneakyThrows
    void getUserBasket_ValidRequest_ReturnsBasketList() {
        List<Basket> baskets = List.of(mock(Basket.class));
        List<BasketDto> dtos = List.of(mock(BasketDto.class));

        when(basketService.getUserBasket(USER_ID)).thenReturn(baskets);
        when(basketMapper.toDto(baskets)).thenReturn(dtos);

        mockMvc.perform(get(BASE_URL)
                .header(USER_ID_HEADER, USER_ID))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(dtos)));
    }

    @Test
    @SneakyThrows
    void removeFromBasket_Success_ReturnsNoContent() {
        mockMvc.perform(delete(BASE_URL)
                .header(USER_ID_HEADER, USER_ID)
                .param(PRODUCT_ID_PARAM, PRODUCT_ID.toString()))
            .andExpect(status().isNoContent());

        verify(basketService).removeFromBasket(USER_ID, PRODUCT_ID);
    }

    @Test
    @SneakyThrows
    void removeFromBasket_ProductNotFound_ReturnsNotFound() {
        String errorMessage = "Product not in basket";
        doThrow(new NotFoundException(errorMessage))
            .when(basketService).removeFromBasket(USER_ID, PRODUCT_ID);

        mockMvc.perform(delete(BASE_URL)
                .header(USER_ID_HEADER, USER_ID)
                .param(PRODUCT_ID_PARAM, PRODUCT_ID.toString()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    @SneakyThrows
    void addToBasket_MissingUserId_ReturnsBadRequest() {
        mockMvc.perform(post(BASE_URL)
                .param(PRODUCT_ID_PARAM, PRODUCT_ID.toString())
                .param(QUANTITY_PARAM, "1"))
            .andExpect(status().isBadRequest());
    }

}