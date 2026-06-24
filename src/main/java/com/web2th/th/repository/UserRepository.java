package com.web2th.th.repository;

import com.web2th.th.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository
        extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByUsernameAndPassword(
            String username,
            String password
    );
}