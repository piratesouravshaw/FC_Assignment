package com.firstclub.membership.dto;

import com.firstclub.membership.model.Membership;
import com.firstclub.membership.model.MembershipPlan;
import com.firstclub.membership.model.MembershipTier;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MembershipDetails {
    private Long membershipId;
    private MembershipPlan.PlanName planName;
    private MembershipTier.TierName tierName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Membership.MembershipStatus status;
}
