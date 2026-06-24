package com.web2th.th.repository;

import com.web2th.th.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface ProductRepository


        extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String keyword);

    List<Product> findByCategoryId(Long categoryId);
    Page<Product> findAll(Pageable pageable);

Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

Page<Product> findByNameContainingIgnoreCase(
        String keyword,
        Pageable pageable

 
);
}