package com.Alizone.Dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Halk3DRequestDto {
	private String total;
    private String installment;
    private String currencyCode;
    private String merchantKey;
    private String merchantId;
    private String invoiceId;
    private String hash;
    private String successUrl;
    private String failUrl;

}
