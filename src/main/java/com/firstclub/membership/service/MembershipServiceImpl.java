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
        User user = userRepository.findById(subscriptionRequest.getUserId())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setId(subscriptionRequest.getUserId());
                    newUser.setName("Test User");
                    return userRepository.save(newUser);
                });

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
}
