package com.Alizone.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.Alizone.Dto.DtoBasketItem;
import com.Alizone.Dto.DtoBasketItemRequest;
import com.Alizone.Dto.DtoProduct;
import com.Alizone.Dto.DtoUserProfile;
import com.Alizone.Entity.Basket;
import com.Alizone.Entity.BasketItem;
import com.Alizone.Entity.Product;
import com.Alizone.Entity.User;
import com.Alizone.Exception.BusinessException;
import com.Alizone.Repository.BasketItemRepository;
import com.Alizone.Repository.UserRepository;

@Service
public class BasketItemService implements IBasketItemService {

    @Autowired
    private IUserService userService;

    @Autowired
    private IBasketService basketService;

    @Autowired
    private IProductService productService;

    @Autowired
    private BasketItemRepository basketItemRepository;
    
    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("isAuthenticated()")
    @Override
    @Transactional
    public DtoBasketItem saveBasketitem(DtoBasketItemRequest request) {

        User user = getAuthenticatedUser();
        Basket basket = basketService.getOrCreateBasketByUser(user);
        Product product = productService.findEntityById(request.getProductId());

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new BusinessException("Geçersiz adet");
        }

        if (request.getQuantity() > product.getStokAdeti()) {
            throw new BusinessException("Belirtilen miktar stokta yok");
        }

        List<BasketItem> allItems =
            basketItemRepository.findAllByBasketAndProduct(basket, product);

        // 🔥 Daha önce eklenmiş ürün varsa
        if (!allItems.isEmpty()) {
            BasketItem item = allItems.get(0);

            item.setActive(true); 
            item.setAdet(request.getQuantity().intValue()); 
            item.setFiyat(product.getFiyat());

            basketItemRepository.save(item);
            return mapToDto(item, basket);
        }

        
        BasketItem basketItem = new BasketItem();
        basketItem.setBasket(basket);
        basketItem.setProduct(product);
        basketItem.setAdet(request.getQuantity().intValue());
        basketItem.setFiyat(product.getFiyat());
        basketItem.setOlusturmatarihi(LocalDateTime.now());
        basketItem.setActive(true);

        basketItemRepository.save(basketItem);
        return mapToDto(basketItem, basket);
    }

    @Override
    public List<DtoBasketItem> findBasketItem() {
        User user = getAuthenticatedUser();
        Basket basket = basketService.getOrCreateBasketByUser(user);

        
        List<BasketItem> basketItems = basket.getBasketItems().stream()
                .filter(BasketItem::isActive)
                .toList();

        return basketItems.stream()
                .map(item -> mapToDto(item, basket))
                .toList();
    }

    @Transactional
    @Override
    public Basket deleteBasketItem(Long id) {
        User user = getAuthenticatedUser();
        Basket basket = basketService.getOrCreateBasketByUser(user);

        
        BasketItem basketItem = basket.getBasketItems().stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Sepet öğesi bulunamadı"));

        
        if (!basketItem.getBasket().getId().equals(basket.getId())) {
            throw new BusinessException("Bu sepet öğesine erişim yetkiniz yok");
        }

        
        basketItem.setActive(false);
        basketItemRepository.save(basketItem);

        return basket;
    }

   
    private DtoBasketItem mapToDto(BasketItem basketItem, Basket basket) {
        DtoBasketItem dto = new DtoBasketItem();
        
        Product product=basketItem.getProduct();
        
        
        dto.setProductId(product.getId());
        dto.setProductIsim(product.getIsim());
        dto.setFiyat(basketItem.getFiyat());
        dto.setAdet(basketItem.getAdet());
        dto.setBasketid(basket.getId());
        dto.setBasketItemId(basketItem.getId());

       
        if (product.getResimler() != null && !product.getResimler().isEmpty()) {
            dto.setImageUrl(product.getResimler()); 
        } else {
            dto.setImageUrl(null); 
        }
        
        return dto;
    }

    
    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new BusinessException("Lütfen giriş yapınız.");
        }
        Object principal = auth.getPrincipal();
        String email;
        if (principal instanceof User) email = ((User) principal).getEmail();
        else if (principal instanceof UserDetails) email = ((UserDetails) principal).getUsername();
        else throw new RuntimeException("Bilinmeyen principal tipi");
        return userService.getUserbyEmail(email);
    }

    @Override
    public ResponseEntity<DtoUserProfile> getProfile() {
        User user = getAuthenticatedUser();

        DtoUserProfile dto = new DtoUserProfile();
        dto.setIsim(user.getIsim());
        dto.setSoyisim(user.getSoyisim());
        dto.setEmail(user.getEmail());
        dto.setTelno(user.getTelno());

        return ResponseEntity.ok(dto);
    }
    

		 
		 
		 
		 
		
		
	}



