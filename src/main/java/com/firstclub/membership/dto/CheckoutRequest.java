package com.firstclub.membership.dto;

import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class CheckoutRequest {
    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @Min(value = 1, message = "Cart total must be greater than 0")
    private double cartTotal;
}