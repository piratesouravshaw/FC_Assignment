package com.firstclub.membership.controller;

import com.firstclub.membership.dto.MembershipDetails;
import com.firstclub.membership.dto.SubscriptionRequest;
import com.firstclub.membership.model.MembershipPlan;
import com.firstclub.membership.model.MembershipTier;
import com.firstclub.membership.service.MembershipService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memberships")
public class MembershipController {

    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @GetMapping("/plans")
    public List<MembershipPlan> getMembershipPlans() {
        return membershipService.getMembershipPlans();
    }

    @GetMapping("/tiers")
    public List<MembershipTier> getMembershipTiers() {
        return membershipService.getMembershipTiers();
    }

    @PostMapping("/subscribe")
    public MembershipDetails subscribe(@RequestBody SubscriptionRequest subscriptionRequest) {
        return membershipService.subscribe(subscriptionRequest);
    }

    @PutMapping("/upgrade")
    public MembershipDetails upgradeSubscription(@RequestBody SubscriptionRequest subscriptionRequest) {
        return membershipService.upgradeSubscription(subscriptionRequest);
    }

    @PutMapping("/downgrade")
    public MembershipDetails downgradeSubscription(@RequestBody SubscriptionRequest subscriptionRequest) {
        return membershipService.downgradeSubscription(subscriptionRequest);
    }

    @DeleteMapping("/cancel/{userId}")
    public void cancelSubscription(@PathVariable Long userId) {
        membershipService.cancelSubscription(userId);
    }

    @GetMapping("/{userId}")
    public MembershipDetails getMembershipDetails(@PathVariable Long userId) {
        return membershipService.getMembershipDetails(userId);
    }
}
