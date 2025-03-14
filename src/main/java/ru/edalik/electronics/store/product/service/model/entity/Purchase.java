package ru.edalik.electronics.store.product.service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "purchase")
public class Purchase {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @JoinColumn(name = "product_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Product product;

    @Column(name = "count")
    private Integer count;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @CreationTimestamp
    @Column(name = "purchase_time")
    private LocalDateTime purchaseTime;

}