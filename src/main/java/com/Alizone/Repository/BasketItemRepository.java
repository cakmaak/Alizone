package com.Alizone.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Alizone.Entity.Basket;
import com.Alizone.Entity.BasketItem;
import com.Alizone.Entity.Product;

@Repository
public interface BasketItemRepository extends JpaRepository<BasketItem, Long> {
	
	//BasketItem findByBasketAndProduct(Basket basket, Product product);
	List<BasketItem> findAllByBasketAndProductAndIsActiveTrue(
		    Basket basket,
		    Product product
		);
	 List<BasketItem> findAllByBasketAndProduct(
		        Basket basket,
		        Product product
		    );
	 List<BasketItem> findAllByBasketAndIsActiveTrue(Basket basket);



	

}
