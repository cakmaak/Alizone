package com.Alizone.Dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item3D {

    private String name;
    private Double price;
    private Integer quantity;
    private String description;
}