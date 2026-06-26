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
public class MembershipPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private PlanName name;
    private double price;

    public MembershipPlan(PlanName name, double price) {
        this.name = name;
        this.price = price;
    }

    public MembershipPlan() {

    }

    public enum PlanName {
        MONTHLY,
        QUARTERLY,
        YEARLY
    }
}
