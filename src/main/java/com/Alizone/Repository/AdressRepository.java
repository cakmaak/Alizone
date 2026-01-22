package com.Alizone.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Alizone.Entity.Address;
import com.Alizone.Entity.User;

public interface AdressRepository extends JpaRepository<Address, Long> {
	
	List<Address> findByUser(User user);
	Optional<Address> findByIdAndUserId(Long id, Long userId);


}
