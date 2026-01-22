package com.Alizone.Service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.Alizone.Dto.DtoBasketItem;
import com.Alizone.Dto.DtoBasketItemRequest;
import com.Alizone.Dto.DtoUserProfile;
import com.Alizone.Entity.Basket;
import com.Alizone.Entity.BasketItem;
import com.Alizone.Entity.User;



public interface IBasketItemService {
	
	public DtoBasketItem saveBasketitem(DtoBasketItemRequest request);
	public List<DtoBasketItem> findBasketItem();
	public Basket deleteBasketItem(Long id);
	public ResponseEntity<DtoUserProfile> getProfile();
	

}
