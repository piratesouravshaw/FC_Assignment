package com.firstclub.membership.service;


import com.firstclub.membership.dto.MembershipDetails;
import com.firstclub.membership.dto.SubscriptionRequest;
import com.firstclub.membership.model.Membership;
import com.firstclub.membership.model.MembershipPlan;
import com.firstclub.membership.model.MembershipTier;
import com.firstclub.membership.model.User;
import com.firstclub.membership.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MembershipServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private MembershipRepository membershipRepository;
    @Mock private MembershipPlanRepository membershipPlanRepository;
    @Mock private MembershipTierRepository membershipTierRepository;

    @InjectMocks
    private MembershipServiceImpl membershipService;

    private SubscriptionRequest request;
    private User testUser;
    private MembershipPlan monthlyPlan;
    private MembershipTier goldTier;

    @BeforeEach
    void setUp() {
        request = new SubscriptionRequest();
        request.setUserId(1L);
        request.setPlanName(MembershipPlan.PlanName.MONTHLY);
        request.setTierName(MembershipTier.TierName.GOLD);

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");

        monthlyPlan = new MembershipPlan(MembershipPlan.PlanName.MONTHLY, 100.0);
        goldTier = new MembershipTier(MembershipTier.TierName.GOLD);
    }

    @Test
    void testSubscribe_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(membershipPlanRepository.findByName(MembershipPlan.PlanName.MONTHLY)).thenReturn(Optional.of(monthlyPlan));
        when(membershipTierRepository.findByName(MembershipTier.TierName.GOLD)).thenReturn(Optional.of(goldTier));
        when(membershipRepository.save(any(Membership.class))).thenAnswer(i -> i.getArguments()[0]);

        MembershipDetails details = membershipService.subscribe(request);

        assertNotNull(details);
        assertEquals(MembershipPlan.PlanName.MONTHLY, details.getPlanName());
        assertEquals(MembershipTier.TierName.GOLD, details.getTierName());
        assertEquals(Membership.MembershipStatus.ACTIVE, details.getStatus());
        verify(membershipRepository, times(1)).save(any(Membership.class));
    }
}
