package com.Alizone.Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;


@Data
public class DtoOrder {
	
    private Long orderId;
    private LocalDateTime tarih;
    private BigDecimal toplamTutar;
    private String status;
    private List<DtoOrderItem> items;
    private DtoAddress teslimatAdresi;
    

}
