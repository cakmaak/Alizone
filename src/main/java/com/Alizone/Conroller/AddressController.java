package com.Alizone.Conroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Alizone.Dto.DtoAddress;
import com.Alizone.Entity.Address;
import com.Alizone.Service.IAddressService;

@RestController
@RequestMapping("/alizone")
public class AddressController implements IAdressController {
	@Autowired
	private IAddressService addressService;
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/add/address")
	@Override
	public Address saveAddress(@RequestBody Address request) {
		
		return addressService.saveAddress(request);
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/findaddress/{addressId}")
	@Override
	public Address findById(@PathVariable Long addressId) {
		// TODO Auto-generated method stub
		return  addressService.findById(addressId);
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/getmyaddress")
	@Override
	public List<Address> getalladres() {
		
		return addressService.getalladres();
	}


	
	@Override
	public ResponseEntity<Void> deleteMyAdres(@PathVariable Long id) {
	    addressService.deleteMyAdres(id);
	    return ResponseEntity.noContent().build();
	}
	
	@PutMapping("/updtadres/{id}")
	@PreAuthorize("isAuthenticated()")
	@Override
	public Address updatemyadres(@PathVariable Long id,@RequestBody DtoAddress newadres) {
		
		return addressService.updatemyadres(id, newadres);
	}





}
