package com.kr.neshop.user_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kr.neshop.user_service.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	 @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'ROLE_SELLER'")
	    List<User> findAllSellers();
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
