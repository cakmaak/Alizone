package com.Alizone.Service;

import java.util.List;

import com.Alizone.Dto.DtoAddress;
import com.Alizone.Entity.Address;
import com.Alizone.Entity.User;

public interface IAddressService {
	public Address saveAddress(Address request);
	public Address findById(Long addressId);
	public List<Address> getalladres();
	public void deleteMyAdres(Long id);
	public Address updatemyadres(Long id,DtoAddress newadres);

}
