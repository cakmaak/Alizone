package com.Alizone.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.Alizone.Entity.Basket;
import com.Alizone.Entity.BasketItem;
import com.Alizone.Entity.User;
import com.Alizone.Exception.BusinessException;
import com.Alizone.Repository.BasketItemRepository;
import com.Alizone.Repository.BasketRepository;

@Service
public class BasketService implements IBasketService {
	
	@Autowired
	private BasketRepository basketRepository;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private BasketItemRepository basketItemRepository;

	@Override
	public Basket getOrCreateBasketByUser(User user) {
		Optional<Basket> optionalBasket=basketRepository.findByUser(user);
		if (optionalBasket.isPresent()) {
			return optionalBasket.get();
			
		}
		Basket newbasket=new Basket();
		newbasket.setUser(user);
		newbasket.setIsactive(true);
		newbasket.setOlusturmatarihi(LocalDateTime.now());

		basketRepository.save(newbasket);
		
		
		return newbasket ;
	}
		
		@Override
	  public List<BasketItem> getBasketItemsForUser(User user) {
			 Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			    if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
			        throw new BusinessException("Lütfen giriş yapınız.");
			    }

			    // 2️ Kullanıcıyı al
			    Object principal = auth.getPrincipal();
			    String email;
			    if (principal instanceof UserDetails) {
			        email = ((UserDetails) principal).getUsername();
			    } else if (principal instanceof User) {
			        email = ((User) principal).getEmail();
			    } else {
			        throw new BusinessException("Bilinmeyen principal tipi");
			    }

			    
	        Basket basket = getOrCreateBasketByUser(user);
	        return basket.getBasketItems().stream()
	                .filter(BasketItem::isActive) // sadece aktifleri al
	                .toList();
	    }
	

	@Override
	public int setquantityinbasket(Long basketitemid, int newquantity) {
		 Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		    if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
		        throw new BusinessException("Lütfen giriş yapınız.");
		    }

		    // 2️ Kullanıcıyı al
		    Object principal = auth.getPrincipal();
		    String email;
		    if (principal instanceof UserDetails) {
		        email = ((UserDetails) principal).getUsername();
		    } else if (principal instanceof User) {
		        email = ((User) principal).getEmail();
		    } else {
		        throw new BusinessException("Bilinmeyen principal tipi");
		    }

		    User user = userService.getUserbyEmail(email);

		    // 3️ Sepeti al veya oluştur
		    Basket basket = this.getOrCreateBasketByUser(user);

		    // 4️ BasketItem var mı kontrol et
		    BasketItem item = basketItemRepository.findById(basketitemid)
		            .orElseThrow(() -> new BusinessException("Ürün bulunamadı."));

		    // 5️ Sepet sahipliği kontrolü
		    if (!item.getBasket().getId().equals(basket.getId())) {
		        throw new BusinessException("Bu ürün sizin sepetinize ait değil.");
		    }

		    // 6️ Quantity 0 veya negatifse item sil
		    if (newquantity <= 0) {
		        basketItemRepository.delete(item);
		        return 0;
		    }

		    // 7️ Stok kontrolü
		    if (newquantity > item.getProduct().getStokAdeti()) {
		        throw new BusinessException("İstenen miktar stokta yok. Maksimum: " + item.getProduct().getStokAdeti());
		    }

		    // 8️ Quantity güncelleme
		    item.setAdet(newquantity);
		    basketItemRepository.save(item);

		    return newquantity;
	}
	

}
