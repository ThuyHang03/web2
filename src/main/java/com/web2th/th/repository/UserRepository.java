package com.web2th.th.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.web2th.th.entity.User;

public interface UserRepository
        extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByUsernameAndPassword(
            String username,
            String password
    );
}