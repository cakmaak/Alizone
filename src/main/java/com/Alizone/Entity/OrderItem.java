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
@Table(name="orderitem",schema = "alizone")
public class OrderItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "adet",nullable = false)
	private int adet;
	
	@Column(name = "toplamfiyat",nullable = false)
	private BigDecimal toplamfiyat;
	
	@Column(name = "eklenmetarihi",nullable = false)
	private LocalDateTime eklenmetarihi=LocalDateTime.now();
	
	@PrePersist
	protected void oncreate() {
		eklenmetarihi=LocalDateTime.now();
	}
	
	@Column(name = "indirim")
	private int indirim;
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "order_id")
	private Order order ;
	
	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;
	
	@Column(name = "image_url", length = 500)
	private String imageurl;
	

}
