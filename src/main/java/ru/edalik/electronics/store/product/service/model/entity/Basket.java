package ru.edalik.electronics.store.product.service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "basket")
public class Basket {

    @EmbeddedId
    private BasketId compositeKey;

    @Column(name = "quantity")
    private Integer quantity;

    @Embeddable
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BasketId {

        @Column(name = "user_id")
        private UUID userId;

        @OneToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "product_id")
        private Product product;

    }

}