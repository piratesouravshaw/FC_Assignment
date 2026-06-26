package com.firstclub.membership.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class MembershipTier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TierName name;

    public MembershipTier(TierName name) {
        this.name = name;
    }

    public MembershipTier() {

    }

    public enum TierName {
        SILVER,
        GOLD,
        PLATINUM
    }
}
