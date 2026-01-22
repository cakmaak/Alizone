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
public class Enerji {
	
	private String sogutmaSinifi;
    private String isitmaSinifi;
    private Double seer;
    private Double scop;

}
