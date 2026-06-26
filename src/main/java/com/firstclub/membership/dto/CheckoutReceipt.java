package com.firstclub.membership.dto;

import lombok.Data;
import java.util.List;

@Data
public class CheckoutReceipt {
    private Long orderId;
    private double originalTotal;
    private double discountAmount;
    private double finalTotal;
    private List<String> appliedBenefits;
    private String message;
}