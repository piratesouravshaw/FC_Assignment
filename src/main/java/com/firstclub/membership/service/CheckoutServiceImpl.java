package com.firstclub.membership.service;

import com.firstclub.membership.dto.CheckoutReceipt;
import com.firstclub.membership.dto.CheckoutRequest;
import com.firstclub.membership.model.Membership;
import com.firstclub.membership.model.MembershipTier;
import com.firstclub.membership.model.Order;
import com.firstclub.membership.model.User;
import com.firstclub.membership.repository.MembershipRepository;
import com.firstclub.membership.repository.OrderRepository;
import com.firstclub.membership.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CheckoutServiceImpl {

    private final OrderRepository orderRepository;
    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;

    public CheckoutServiceImpl(OrderRepository orderRepository, MembershipRepository membershipRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
    }

    public CheckoutReceipt processCheckout(CheckoutRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        double discountPercentage = 0.0;
        List<String> benefits = new ArrayList<>();
        String message = "Thanks for shopping!";

        // 1. Check for Active Membership and apply Business Rules
        Optional<Membership> activeMembership = membershipRepository.findByUserIdAndStatus(
                user.getId(), Membership.MembershipStatus.ACTIVE);

        if (activeMembership.isPresent()) {
            MembershipTier.TierName tier = activeMembership.get().getTier().getName();

            switch (tier) {
                case SILVER:
                    discountPercentage = 0.05; // 5% discount
                    benefits.add("5% Silver Tier Discount");
                    break;
                case GOLD:
                    discountPercentage = 0.10; // 10% discount
                    benefits.add("10% Gold Tier Discount");
                    benefits.add("Free Standard Delivery");
                    break;
                case PLATINUM:
                    discountPercentage = 0.15; // 15% discount
                    benefits.add("15% Platinum Tier Discount");
                    benefits.add("Free Priority Delivery");
                    benefits.add("Exclusive Priority Support");
                    break;
            }
            message = "Thanks for being a " + tier + " member! Enjoy your perks.";
        }

        // 2. Calculate Totals
        double discountAmount = request.getCartTotal() * discountPercentage;
        double finalTotal = request.getCartTotal() - discountAmount;

        // 3. Save the Order to the database
        Order order = new Order();
        order.setUser(user);
        order.setOrderValue(finalTotal);
        order.setOrderDate(LocalDate.now());

        Order savedOrder = orderRepository.save(order);

        // 4. Generate the Receipt
        CheckoutReceipt receipt = new CheckoutReceipt();
        receipt.setOrderId(savedOrder.getId());
        receipt.setOriginalTotal(request.getCartTotal());
        receipt.setDiscountAmount(discountAmount);
        receipt.setFinalTotal(finalTotal);
        receipt.setAppliedBenefits(benefits);
        receipt.setMessage(message);

        return receipt;
    }
}