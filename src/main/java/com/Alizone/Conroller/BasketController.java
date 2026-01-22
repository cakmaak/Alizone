package com.Alizone.Conroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Alizone.Service.IBasketService;

@RestController
@RequestMapping("/alizone")
public class BasketController implements IBasketController {
	@Autowired
	IBasketService basketService;
	
	@PreAuthorize("isAuthenticated()")
	@PutMapping("/setquantity/{basketitemid}")
	@Override
	public int setquantityinbasket(@PathVariable Long basketitemid,@RequestBody int newquantity) {
		
		return basketService.setquantityinbasket(basketitemid, newquantity);
	}

}
