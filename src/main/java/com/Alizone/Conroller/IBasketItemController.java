package com.Alizone.Conroller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.Alizone.Dto.DtoBasketItem;
import com.Alizone.Dto.DtoBasketItemRequest;
import com.Alizone.Dto.DtoUserProfile;
import com.Alizone.Entity.Basket;
import com.Alizone.Entity.BasketItem;


public interface IBasketItemController {
	public DtoBasketItem saveBasketitem(DtoBasketItemRequest request);
	public Basket deleteBasketItem(Long id);
	public List<DtoBasketItem> findBasketItem();
	 public ResponseEntity<DtoUserProfile> getProfile();
}
	


