package com.nus.sellr.order.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Document(collection = "orders")
public class Order {
    @Id
    private String id;

    private String userId; // Link cart to a specific user
    private List<OrderItem> items = new ArrayList<>();
    private BigDecimal orderPrice;
    private LocalDateTime createdAt;

    public Order() {
        this.createdAt = LocalDateTime.now();
    }
    public Order(String userId) {
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }
}
