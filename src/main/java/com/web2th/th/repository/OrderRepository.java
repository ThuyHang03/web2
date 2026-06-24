package com.web2th.th.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.web2th.th.model.Order;

public interface OrderRepository
        extends JpaRepository<Order, Long> {

    List<Order> findByUsername(String username);

}