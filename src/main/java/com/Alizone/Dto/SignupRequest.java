package com.Alizone.Dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class SignupRequest {
	
	
	private String isim;
	

	private String soyisim;
	

	private String telno;
	
	
	private String email;
	
	
	private String password;
	
	private Boolean kvkkAccepted; 
	
	

}
