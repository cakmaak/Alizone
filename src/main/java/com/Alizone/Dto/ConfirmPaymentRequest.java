package com.Alizone.Dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConfirmPaymentRequest {
	
	private Double total;
    private String invoice_id;
    private String status;
    private String merchant_key;
    private String hash_key;

}
