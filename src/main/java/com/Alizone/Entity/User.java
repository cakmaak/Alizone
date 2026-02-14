package com.Alizone.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.Alizone.Enum.ROL;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "kullanıcılar",schema = "alizone")
public class User {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	
	@Column(name = "isim",nullable = false)
	private String isim;
	
	
	@Column(name ="soyisim",nullable = false)
	private String soyisim;
	
	@Column(name = "telno",nullable = false,unique = true)
	private String telno;
	
	@Column(name = "email",nullable = false,unique = true)
	private String email;
	
	@Column(name = "password",nullable = false)
	private String password;
	
	@Column(name = "kayittarihi")
	private LocalDateTime kayittarihi;
	
	@Enumerated(EnumType.STRING)
	private ROL vasıf;
	
	private String resetToken;
	
	private LocalDateTime resetTokenExpiry;
	
	@Column(name = "kvkk_accepted",nullable = false)
	private Boolean kvkkAccepted;

	@Column(name = "kvkk_accepted_at",nullable = false)
	private LocalDateTime kvkkAcceptedAt;

	@Column(name = "kvkk_accepted_ip",nullable = false)
	private String kvkkAcceptedIp;

	
	@PrePersist
    protected void onCreate() {
        kayittarihi = LocalDateTime.now();
    }
	
	@OneToMany(
	        mappedBy = "user",
	        cascade = CascadeType.ALL,
	        orphanRemoval = true
	    )
		@JsonManagedReference
	    private List<Address> adresler = new ArrayList<>();
	

	
	

}
