package com.Alizone.Service;

import java.util.List;

import com.Alizone.Entity.Basket;
import com.Alizone.Entity.BasketItem;
import com.Alizone.Entity.User;

public interface IBasketService {
	
	public Basket getOrCreateBasketByUser(User user);
	public int setquantityinbasket(Long basketitemid,  int newquantity); 
	public List<BasketItem> getBasketItemsForUser(User user);

}
