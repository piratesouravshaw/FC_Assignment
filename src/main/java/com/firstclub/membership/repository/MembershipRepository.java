package com.firstclub.membership.repository;

import com.firstclub.membership.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    Optional<Membership> findByUserIdAndStatus(Long userId, Membership.MembershipStatus status);
    List<Membership> findByStatus(Membership.MembershipStatus status);
}
