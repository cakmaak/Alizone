package com.Alizone.Dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class DtoBasketItem {
	
	 private  Long basketItemId;
	 private Long productId;
	 private String productIsim;
	 private BigDecimal fiyat;
	 private int adet;
	 private int indirim;
	 private  Long basketid;
	 private List<String> imageUrl;

}
