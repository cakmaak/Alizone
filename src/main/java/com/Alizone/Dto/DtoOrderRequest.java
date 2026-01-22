package com.Alizone.Dto;

import com.Alizone.Entity.Address;

import lombok.Data;

@Data
public class DtoOrderRequest {
	
    private Long addressId;   
    private Address address;
    private Boolean contractsAccepted;

}
