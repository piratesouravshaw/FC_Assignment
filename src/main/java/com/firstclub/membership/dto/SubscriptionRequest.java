package com.firstclub.membership.dto;

import com.firstclub.membership.model.MembershipPlan;
import com.firstclub.membership.model.MembershipTier;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SubscriptionRequest {
    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Plan Name cannot be null")
    private MembershipPlan.PlanName planName;

    @NotNull(message = "Tier Name cannot be null")
    private MembershipTier.TierName tierName;
}