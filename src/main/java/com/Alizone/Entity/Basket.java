package com.Alizone.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "basket",schema = "alizone")
public class Basket {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "is_active")
	private boolean isactive;
	
	
	@Column(name = "olusturmatarihi")
	private LocalDateTime olusturmatarihi=LocalDateTime.now();
	
	
	 @PrePersist
	    protected void onCreate() {
	        olusturmatarihi = LocalDateTime.now();
	        isactive = true;
	    }
	 
	  @JsonManagedReference
	    @OneToMany(mappedBy = "basket", cascade = CascadeType.ALL,orphanRemoval = true)
	    private List<BasketItem> basketItems = new ArrayList<>();

	    @ManyToOne
	    @JoinColumn(name = "user_id")
	    private User user;

}
