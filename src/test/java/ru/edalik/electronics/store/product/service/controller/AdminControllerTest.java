package ru.edalik.electronics.store.product.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.edalik.electronics.store.product.service.mapper.CategoryMapper;
import ru.edalik.electronics.store.product.service.mapper.ProductMapper;
import ru.edalik.electronics.store.product.service.model.dto.CategoryDto;
import ru.edalik.electronics.store.product.service.model.dto.ProductDto;
import ru.edalik.electronics.store.product.service.model.entity.Category;
import ru.edalik.electronics.store.product.service.model.entity.Product;
import ru.edalik.electronics.store.product.service.model.exception.NotFoundException;
import ru.edalik.electronics.store.product.service.service.interfaces.AdminService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    static final String BASE_URL = "/api/v1/products/admin";
    static final UUID PRODUCT_ID = UUID.randomUUID();
    static final String NAME = "Name";
    static final CategoryDto CATEGORY = new CategoryDto("Category");
    static final String DESCRIPTION = "Description";
    static final BigDecimal PRICE = BigDecimal.ONE;
    static final Integer QUANTITY = 1;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AdminService adminService;

    @MockitoBean
    ProductMapper productMapper;

    @MockitoBean
    CategoryMapper categoryMapper;

    @Test
    void upsertProduct_ValidInput_ReturnsProductDto() throws Exception {
        ProductDto requestDto = new ProductDto(
            PRODUCT_ID,
            NAME,
            CATEGORY,
            DESCRIPTION,
            PRICE,
            QUANTITY
        );
        Product product = mock(Product.class);
        ProductDto responseDto = mock(ProductDto.class);

        when(productMapper.toEntity(requestDto)).thenReturn(product);
        when(adminService.upsertProduct(product)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(responseDto);

        MvcResult result = mockMvc.perform(
                post(
                    BASE_URL + "/product")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)
                    )
            )
            .andExpect(status().isOk())
            .andReturn();

        assertEquals(objectMapper.writeValueAsString(responseDto), result.getResponse().getContentAsString());
    }

    @Test
    void upsertProduct_InvalidInput_ReturnsValidationErrors() throws Exception {
        ProductDto invalidDto = new ProductDto(
            null,
            "",
            CATEGORY,
            DESCRIPTION,
            BigDecimal.valueOf(-1),
            -5
        );

        mockMvc.perform(
                post(
                    BASE_URL + "/product")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDto)
                    )
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fields[*].field").exists());
    }

    @Test
    void deleteProductById_ExistingProduct_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/product/{id}", PRODUCT_ID))
            .andExpect(status().isNoContent());

        verify(adminService).deleteProductById(PRODUCT_ID);
    }

    @Test
    void deleteProductById_NotExistingProduct_ReturnsNotFound() throws Exception {
        String errorMessage = "Product not found";
        doThrow(new NotFoundException(errorMessage)).when(adminService).deleteProductById(PRODUCT_ID);

        mockMvc.perform(delete(BASE_URL + "/product/{id}", PRODUCT_ID))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void upsertCategory_ValidInput_ReturnsCategoryDto() throws Exception {
        Category category = mock(Category.class);
        CategoryDto responseDto = mock(CategoryDto.class);

        when(categoryMapper.toEntity(CATEGORY)).thenReturn(category);
        when(adminService.addCategory(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(responseDto);

        mockMvc.perform(
                put(
                    BASE_URL + "/category")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(CATEGORY)
                    )
            )
            .andExpect(status().isOk());
    }

    @Test
    void upsertCategory_InvalidInput_ReturnsValidationErrors() throws Exception {
        CategoryDto invalidDto = new CategoryDto("");

        mockMvc.perform(
                put(
                    BASE_URL + "/category")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDto)
                    )
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fields[*].field").exists());
    }

}