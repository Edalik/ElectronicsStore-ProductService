package ru.edalik.electronics.store.product.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.edalik.electronics.store.product.service.mapper.ProductMapper;
import ru.edalik.electronics.store.product.service.model.dto.ProductDto;
import ru.edalik.electronics.store.product.service.model.dto.ProductPageDto;
import ru.edalik.electronics.store.product.service.model.dto.ProductSpecificationDto;
import ru.edalik.electronics.store.product.service.model.entity.Product;
import ru.edalik.electronics.store.product.service.model.exception.NotFoundException;
import ru.edalik.electronics.store.product.service.service.interfaces.ProductService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    static final String BASE_URL = "/api/v1/products";
    static final UUID PRODUCT_ID = UUID.randomUUID();

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ProductService productService;

    @MockitoBean
    ProductMapper productMapper;

    @Test
    @SneakyThrows
    void getProductById_ExistingProduct_ReturnsProductDto() {
        Product product = mock(Product.class);
        ProductDto dto = mock(ProductDto.class);

        when(productService.getProductById(PRODUCT_ID)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(dto);

        MvcResult result = mockMvc.perform(get(BASE_URL + "/{id}", PRODUCT_ID))
            .andExpect(status().isOk())
            .andReturn();

        assertEquals(objectMapper.writeValueAsString(dto), result.getResponse().getContentAsString());
    }

    @Test
    @SneakyThrows
    void getProductById_NotExistingProduct_ReturnsNotFound() {
        String errorMessage = "Product not found";
        when(productService.getProductById(PRODUCT_ID)).thenThrow(new NotFoundException(errorMessage));

        mockMvc.perform(get(BASE_URL + "/{id}", PRODUCT_ID))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    @SneakyThrows
    void getProducts_WithSpecification_ReturnsProductPage() {
        ProductSpecificationDto specDto = ProductSpecificationDto.builder().build();
        ProductPageDto pageDto = mock(ProductPageDto.class);

        when(productService.getProducts(specDto)).thenReturn(pageDto);

        MvcResult result = mockMvc.perform(
                get(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(specDto)
                    )
            )
            .andExpect(status().isOk())
            .andReturn();

        assertEquals(objectMapper.writeValueAsString(pageDto), result.getResponse().getContentAsString());
    }

    @Test
    @SneakyThrows
    void getProducts_WithoutSpecification_ReturnsProductPage() {
        ProductPageDto pageDto = mock(ProductPageDto.class);

        when(productService.getProducts(null)).thenReturn(pageDto);

        MvcResult result = mockMvc.perform(get(BASE_URL))
            .andExpect(status().isOk())
            .andReturn();

        assertEquals(objectMapper.writeValueAsString(pageDto), result.getResponse().getContentAsString());
    }

}