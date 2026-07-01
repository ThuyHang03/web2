package com.web2th.th.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.web2th.th.entity.Category;

public interface CategoryRepository
        extends JpaRepository<Category, Long> {
}