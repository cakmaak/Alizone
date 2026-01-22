package com.Alizone.Conroller;

import java.security.PublicKey;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Alizone.Dto.DtoBasketItem;
import com.Alizone.Dto.DtoBasketItemRequest;
import com.Alizone.Dto.DtoUserProfile;
import com.Alizone.Entity.Basket;
import com.Alizone.Entity.BasketItem;
import com.Alizone.Service.IBasketItemService;
import com.Alizone.Service.IBasketService;

@RestController
@RequestMapping("/alizone")
public class BasketItemController implements IBasketItemController {
	@Autowired
	IBasketItemService basketItemService;

	@Autowired
	IBasketService basketService;

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/savebasketitem")
	@Override
	public DtoBasketItem saveBasketitem(@RequestBody DtoBasketItemRequest request) {

		return basketItemService.saveBasketitem(request);
	}

	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/deleteitem/{id}")
	@Override
	public Basket deleteBasketItem(@PathVariable Long id) {
		return basketItemService.deleteBasketItem(id);

	}

	@GetMapping("/getbasket")
	@PreAuthorize("isAuthenticated()")
	@Override
	public List<DtoBasketItem> findBasketItem() {
		return basketItemService.findBasketItem();
	}
	
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/getprofile")
	@Override
	public ResponseEntity<DtoUserProfile> getProfile() {
		return basketItemService.getProfile();
		
		
	}

}
