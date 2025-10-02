package com.nus.sellr.order.entity;

public enum OrderStatus {
    //for individual order items
    PENDING,
    SHIPPED,
    DELIVERED,

    //for the entire order
    COMPLETED,
    INCOMPLETE
}