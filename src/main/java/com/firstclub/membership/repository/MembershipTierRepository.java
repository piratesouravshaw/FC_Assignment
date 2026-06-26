package com.firstclub.membership.repository;

import com.firstclub.membership.model.MembershipTier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembershipTierRepository extends JpaRepository<MembershipTier, Long> {
    Optional<MembershipTier> findByName(MembershipTier.TierName name);
}
