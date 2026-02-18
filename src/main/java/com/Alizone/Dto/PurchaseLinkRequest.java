package com.Alizone.Dto;



import lombok.Data;

@Data
public class PurchaseLinkRequest {

    private String merchant_key;
    private String currency_code;
    private String invoice;   
    private String name;
    private String surname;
}