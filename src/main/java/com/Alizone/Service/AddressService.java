package com.Alizone.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;

import com.Alizone.Dto.DtoAddress;
import com.Alizone.Entity.Address;
import com.Alizone.Entity.User;
import com.Alizone.Exception.BusinessException;
import com.Alizone.Repository.AdressRepository;

@Service
public class AddressService implements IAddressService {

    private final SecurityFilterChain filterChain;

    @Autowired private AdressRepository adressRepository;
    @Autowired private IUserService userService;

    AddressService(SecurityFilterChain filterChain) {
        this.filterChain = filterChain;
    }

    @Override
    public Address saveAddress(Address request) {
        User user = getAuthenticatedUser();

        if(request.getAdresSatir1() == null || request.getSehir() == null || request.getTelefon() == null) {
            throw new BusinessException("Gerekli tüm alanları doldurun.");
        }
        if (!"ANKARA".equalsIgnoreCase(request.getSehir())) {
            throw new BusinessException(
                "Sadece Ankara adresi ekleyebilirsiniz."
            );
        }

        request.setUser(user);
        return adressRepository.save(request);
    }

    @Override
    public Address findById(Long addressId) {
        return adressRepository.findById(addressId)
                .orElseThrow(() -> new BusinessException("Geçerli bir adres seçiniz"));
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new BusinessException("Lütfen giriş yapınız.");
        }
        Object principal = auth.getPrincipal();
        String email;
        if(principal instanceof User) email = ((User) principal).getEmail();
        else if(principal instanceof UserDetails) email = ((UserDetails) principal).getUsername();
        else throw new BusinessException("Bilinmeyen principal tipi");
        return userService.getUserbyEmail(email);
    }

	@Override
	public List<Address> getalladres() {
		 User user = getAuthenticatedUser();
		    return adressRepository.findByUser(user); 
        
        
		
		
	}

	@Override
	public void deleteMyAdres(Long id) {
	    User user = getAuthenticatedUser();

	    Address address = adressRepository.findById(id)
	        .orElseThrow(() -> new BusinessException("Adres bulunamadı"));

	    if (!address.getUser().getId().equals(user.getId())) {
	        throw new BusinessException("Bu adres size ait değil");
	    }

	    adressRepository.delete(address);
	}

	@Override
	public Address updatemyadres(Long id, DtoAddress newadres) {
	    User user = getAuthenticatedUser();

	    Address address = adressRepository.findById(id)
	        .orElseThrow(() -> new BusinessException("Adres bulunamadı"));

	    if (!address.getUser().getId().equals(user.getId())) {
	        throw new BusinessException("Bu adres size ait değil");
	    }

	    address.setAdresSatir1(newadres.getAdresSatir1());
	    address.setAdresSatir2(newadres.getAdresSatir2());
	    address.setAliciAdiSoyadi(newadres.getAliciAdiSoyadi());

	    
	    address.setFaturaAdiSoyadi(newadres.getFaturaAdiSoyadi());

	    address.setFaturaTipi(newadres.getFaturaTipi());
	    address.setFirmaAdi(newadres.getFirmaAdi());
	    address.setIlce(newadres.getIlce());
	    address.setPostaKodu(newadres.getPostaKodu());
	    address.setSehir(newadres.getSehir());
	    address.setTcKimlikNo(newadres.getTcKimlikNo());
	    address.setUlke(newadres.getUlke());
	    address.setTelefon(newadres.getTelefon());

	    address.setVergiDairesi(newadres.getVergiDairesi());
	    address.setVergiNo(newadres.getVergiNo());

	    return adressRepository.save(address);
	}

	}



