package com.Alizone.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "basketitem",schema = "alizone")
public class BasketItem {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "adet",nullable = false)
	private int adet;
	
	private boolean isActive = true;

	
	private BigDecimal fiyat;
	
	private int indirim;
	
	private LocalDateTime olusturmatarihi=LocalDateTime.now();
	
	
	
	@PrePersist
	protected void onCreate() {
		olusturmatarihi=LocalDateTime.now();
		
	}
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "basket_id")
	private Basket basket;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;
	
			

}
