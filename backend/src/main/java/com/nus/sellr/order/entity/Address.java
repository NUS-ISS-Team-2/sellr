package com.nus.sellr.order.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Address {
    private String fullName;
    private String street;
    private String city;
    private String stateZipCountry;
}