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
public class Gaz {
	
	
private String tur;
private Double miktarKg;
private Integer gwp;
private Double co2EquivalentTon;

}
