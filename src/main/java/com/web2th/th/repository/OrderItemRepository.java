package com.web2th.th.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.web2th.th.entity.OrderItem;

import java.util.List;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);
}