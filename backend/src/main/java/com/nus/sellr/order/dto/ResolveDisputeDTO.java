package com.nus.sellr.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResolveDisputeDTO {
    private String orderId;
    private String productId;
}
