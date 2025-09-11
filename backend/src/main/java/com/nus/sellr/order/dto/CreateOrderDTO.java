package com.nus.sellr.order.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderDTO {
    private String userId;
    private List<OrderItemDTO> items;
}

