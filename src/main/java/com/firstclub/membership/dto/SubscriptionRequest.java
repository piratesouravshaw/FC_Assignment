package com.firstclub.membership.dto;

import com.firstclub.membership.model.MembershipPlan;
import com.firstclub.membership.model.MembershipTier;
import lombok.Data;

@Data
public class SubscriptionRequest {
    private Long userId;
    private MembershipPlan.PlanName planName;
    private MembershipTier.TierName tierName;
}
