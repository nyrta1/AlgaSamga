package com.example.web.repository;

import com.example.web.models.UserEntity;
import jakarta.transaction.Transactional;
import jakarta.validation.Constraint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
    UserEntity findByUsername(String username);
    UserEntity findFirstByUsername(String username);

    List<UserEntity> findByLastActivityBefore(LocalDateTime timestamp);

    long count();
}
