package com.Alizone.Entity.Embeddable;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;



@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Boyutlar {

	  private Integer icGenislik;
	    private Integer icYukseklik;
	    private Integer icDerinlik;

	    
	    private Integer disGenislik;
	    private Integer disYukseklik;
	    private Integer disDerinlik;
}
