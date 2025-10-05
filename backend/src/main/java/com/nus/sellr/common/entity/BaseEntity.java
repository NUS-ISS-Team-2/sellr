package com.nus.sellr.common.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BaseEntity {
    private LocalDateTime dateCreated;
    private String createdBy; // userId of creator for audit
}
