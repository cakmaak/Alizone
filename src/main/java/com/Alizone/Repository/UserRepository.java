package com.Alizone.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Alizone.Entity.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
	Optional<User> findByEmail(String email);
	Optional<User> findByEmailIgnoreCase(String email);
	 Optional<User> findByResetToken(String resetToken);
	 boolean existsByEmail(String email);



}
