package com.nus.sellr.product.entity;

import com.nus.sellr.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "product_reviews")
public class ProductReview extends BaseEntity {

    @Id
    private String id;
    private int rating;
    private String description;

    private String userId;
    private String productId;

}
