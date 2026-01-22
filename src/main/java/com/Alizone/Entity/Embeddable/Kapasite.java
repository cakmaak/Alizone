package com.Alizone.Entity.Embeddable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Kapasite {
	
private Integer sogutmaBtu;
private Integer isitmaBtu;
private Double sogutmaKw;
private Double isitmaKw;

}
