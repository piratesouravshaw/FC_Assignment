package com.firstclub.membership.service;

import com.firstclub.membership.dto.MembershipDetails;
import com.firstclub.membership.dto.SubscriptionRequest;
import com.firstclub.membership.model.*;
import com.firstclub.membership.repository.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MembershipServiceImpl implements MembershipService {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final MembershipTierRepository membershipTierRepository;
    private final OrderRepository orderRepository;

    public MembershipServiceImpl(UserRepository userRepository, MembershipRepository membershipRepository,
                                 MembershipPlanRepository membershipPlanRepository,
                                 MembershipTierRepository membershipTierRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.membershipPlanRepository = membershipPlanRepository;
        this.membershipTierRepository = membershipTierRepository;
        this.orderRepository = orderRepository;
    }

    

    @Override
    public List<MembershipPlan> getMembershipPlans() {
        return membershipPlanRepository.findAll();
    }

    @Override
    public List<MembershipTier> getMembershipTiers() {
        return membershipTierRepository.findAll();
    }

    @Override
    public MembershipDetails subscribe(SubscriptionRequest subscriptionRequest) {

        // 1. ADD THIS CHECK: Prevent multiple active subscriptions
        Optional<Membership> existingActive = membershipRepository.findByUserIdAndStatus(
                subscriptionRequest.getUserId(),
                Membership.MembershipStatus.ACTIVE
        );

        if (existingActive.isPresent()) {
            throw new RuntimeException("User already has an active membership. Please use the upgrade or downgrade API.");
        }

        // 2. Existing user creation/fetch logic
        User user = userRepository.findById(subscriptionRequest.getUserId())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setId(subscriptionRequest.getUserId());
                    newUser.setName("Test User");
                    return userRepository.save(newUser);
                });

        // 3. Existing plan/tier logic
        MembershipPlan plan = membershipPlanRepository.findByName(subscriptionRequest.getPlanName())
                .orElseThrow(() -> new RuntimeException("Plan not found"));
        MembershipTier tier = membershipTierRepository.findByName(subscriptionRequest.getTierName())
                .orElseThrow(() -> new RuntimeException("Tier not found"));

        Membership membership = new Membership();
        membership.setUser(user);
        membership.setPlan(plan);
        membership.setTier(tier);
        membership.setStartDate(LocalDate.now());
        membership.setEndDate(calculateEndDate(plan.getName()));
        membership.setStatus(Membership.MembershipStatus.ACTIVE);

        membershipRepository.save(membership);

        return toMembershipDetails(membership);
    }

    @Override
    public MembershipDetails upgradeSubscription(SubscriptionRequest subscriptionRequest) {
        return updateSubscription(subscriptionRequest);
    }

    @Override
    public MembershipDetails downgradeSubscription(SubscriptionRequest subscriptionRequest) {
        return updateSubscription(subscriptionRequest);
    }

    private MembershipDetails updateSubscription(SubscriptionRequest subscriptionRequest) {
        Membership existingMembership = membershipRepository.findByUserIdAndStatus(subscriptionRequest.getUserId(), Membership.MembershipStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Active membership not found"));

        MembershipPlan plan = membershipPlanRepository.findByName(subscriptionRequest.getPlanName())
                .orElseThrow(() -> new RuntimeException("Plan not found"));
        MembershipTier tier = membershipTierRepository.findByName(subscriptionRequest.getTierName())
                .orElseThrow(() -> new RuntimeException("Tier not found"));

        existingMembership.setPlan(plan);
        existingMembership.setTier(tier);
        existingMembership.setStartDate(LocalDate.now());
        existingMembership.setEndDate(calculateEndDate(plan.getName()));

        membershipRepository.save(existingMembership);

        return toMembershipDetails(existingMembership);
    }

    @Override
    public void cancelSubscription(Long userId) {
        Membership existingMembership = membershipRepository.findByUserIdAndStatus(userId, Membership.MembershipStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Active membership not found"));
        existingMembership.setStatus(Membership.MembershipStatus.CANCELLED);
        membershipRepository.save(existingMembership);
    }

    @Override
    public MembershipDetails getMembershipDetails(Long userId) {
        Membership existingMembership = membershipRepository.findByUserIdAndStatus(userId, Membership.MembershipStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Active membership not found"));
        return toMembershipDetails(existingMembership);
    }

    private LocalDate calculateEndDate(MembershipPlan.PlanName planName) {
        LocalDate now = LocalDate.now();
        switch (planName) {
            case MONTHLY:
                return now.plusMonths(1);
            case QUARTERLY:
                return now.plusMonths(3);
            case YEARLY:
                return now.plusYears(1);
            default:
                throw new RuntimeException("Invalid plan name");
        }
    }

    private MembershipDetails toMembershipDetails(Membership membership) {
        MembershipDetails details = new MembershipDetails();
        details.setMembershipId(membership.getId());
        details.setPlanName(membership.getPlan().getName());
        details.setTierName(membership.getTier().getName());
        details.setStartDate(membership.getStartDate());
        details.setEndDate(membership.getEndDate());
        details.setStatus(membership.getStatus());
        return details;
    }
    @Override
    public void evaluateAndUpgradeTiers() {
        // Fetch all currently active memberships
        List<Membership> activeMemberships = membershipRepository.findByStatus(Membership.MembershipStatus.ACTIVE);

        // Assuming we evaluate based on the current month's orders
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate today = LocalDate.now();

        for (Membership membership : activeMemberships) {
            MembershipTier.TierName currentTierName = membership.getTier().getName();

            // Skip if already at the highest tier
            if (currentTierName == MembershipTier.TierName.PLATINUM) {
                continue;
            }

            // Fetch orders for this user in the current month
            List<Order> userOrders = orderRepository.findByUserIdAndOrderDateBetween(
                    membership.getUser().getId(), startOfMonth, today);

            // Check criteria: >= 2 orders AND total value >= 100
            if (userOrders.size() >= 2) {
                double totalOrderValue = userOrders.stream()
                        .mapToDouble(Order::getOrderValue)
                        .sum();

                if (totalOrderValue >= 100.0) {
                    upgradeUserTier(membership, currentTierName);
                }
            }
        }
    }

    private void upgradeUserTier(Membership membership, MembershipTier.TierName currentTierName) {
        MembershipTier.TierName targetTierName = null;

        if (currentTierName == MembershipTier.TierName.SILVER) {
            targetTierName = MembershipTier.TierName.GOLD;
        } else if (currentTierName == MembershipTier.TierName.GOLD) {
            targetTierName = MembershipTier.TierName.PLATINUM;
        }

        if (targetTierName != null) {
            MembershipTier nextTier = membershipTierRepository.findByName(targetTierName)
                    .orElseThrow(() -> new RuntimeException("Target tier not found in database"));

            membership.setTier(nextTier);
            membershipRepository.save(membership);
        }
    }
}
