package com.Alizone.Dto;

import lombok.Data;

@Data
public class DtoPaymentResponse {
	
    private Long orderId;
    private boolean success;
    private Long timestamp;
    private String signature;
    String paymentId;

}
