package com.Alizone.Conroller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.Alizone.Dto.DtoAddress;
import com.Alizone.Entity.Address;

public interface IAdressController {
	
	public Address saveAddress(Address request);
	Address findById(Long addressId);
	public List<Address> getalladres();
	public ResponseEntity<Void> deleteMyAdres(Long id);
	public Address updatemyadres(Long id,DtoAddress newadres);

}
