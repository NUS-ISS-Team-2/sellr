package com.nus.sellr.wishlist.dto;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class AddToWishlistRequest {
    @NotBlank
    String userId;

    @NotBlank
    String productId;

}
