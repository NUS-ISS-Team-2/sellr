package com.nus.sellr.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisputeRequestDTO {
    private String orderId;
    private String productId;
    private String reason;
    private String description;
}