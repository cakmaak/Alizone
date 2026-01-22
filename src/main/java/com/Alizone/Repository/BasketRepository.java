package com.Alizone.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Alizone.Entity.Basket;
import com.Alizone.Entity.User;


@Repository
public interface BasketRepository extends JpaRepository<Basket, Long> {
	
	Optional<Basket> findByUser(User user);

}
