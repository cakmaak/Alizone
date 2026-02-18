package com.Alizone.Dto;

import lombok.Data;

@Data
public class PurchaseLinkResponse {

    private Boolean status;
    private Integer status_code;
    private String success_message;
    private String link;
    private String order_id;
}