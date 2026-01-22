package com.Alizone.Dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class DtoOrderItem {
	
	
	
	private String ürünismi;
	
	private int adet;
	
	private BigDecimal fiyat;
	
	private int indirim;
	
	private String imageUrl;

}
