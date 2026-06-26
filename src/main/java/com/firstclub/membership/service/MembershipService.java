package com.firstclub.membership.service;

import com.firstclub.membership.dto.MembershipDetails;
import com.firstclub.membership.dto.SubscriptionRequest;
import com.firstclub.membership.model.MembershipPlan;
import com.firstclub.membership.model.MembershipTier;

import java.util.List;

public interface MembershipService {
    List<MembershipPlan> getMembershipPlans();
    List<MembershipTier> getMembershipTiers();
    MembershipDetails subscribe(SubscriptionRequest subscriptionRequest);
    MembershipDetails upgradeSubscription(SubscriptionRequest subscriptionRequest);
    MembershipDetails downgradeSubscription(SubscriptionRequest subscriptionRequest);
    void cancelSubscription(Long userId);
    MembershipDetails getMembershipDetails(Long userId);
}
