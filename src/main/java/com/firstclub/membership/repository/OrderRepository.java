package com.firstclub.membership.repository;

import com.firstclub.membership.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdAndOrderDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}
