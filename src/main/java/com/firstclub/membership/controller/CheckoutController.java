package com.firstclub.membership.controller;

import com.firstclub.membership.dto.CheckoutReceipt;
import com.firstclub.membership.dto.CheckoutRequest;
import com.firstclub.membership.service.CheckoutServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final CheckoutServiceImpl checkoutService;

    public CheckoutController(CheckoutServiceImpl checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping
    public CheckoutReceipt checkout(@Valid @RequestBody CheckoutRequest request) {
        return checkoutService.processCheckout(request);
    }
}