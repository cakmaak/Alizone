package com.Alizone.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.Alizone.Enum.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import java.time.ZonedDateTime;
import java.time.ZoneId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders",schema = "alizone")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "siparis_durumu")
	private OrderStatus siparisdurumu;
	
	@Column(name = "toplamtutar")
	private BigDecimal toplamtutar;
	
	private String paymentLink;
	
	private String kargotakipno;
	
	private String paymentProvider;
	
	private String bankPaymentId;
	
	@Column(name = "olusturma_tarihi")
	private LocalDateTime olusturmatarihi;
	
	@Column(name = "kargoya_verilme_tarihi")
	private LocalDateTime shippedAt;

	@PrePersist
	public void prePersist() {
	    // İstanbul saatine göre oluşturma tarihi
	    this.olusturmatarihi = ZonedDateTime.now(ZoneId.of("Europe/Istanbul")).toLocalDateTime();
	    
	    // Eğer contractsAcceptedAt da set edilecekse, örn:
	    if (this.contractsAccepted != null && this.contractsAccepted && this.contractsAcceptedAt == null) {
	        this.contractsAcceptedAt = ZonedDateTime.now(ZoneId.of("Europe/Istanbul")).toLocalDateTime();
	    }
	}
	
	private Boolean contractsAccepted;
	private LocalDateTime contractsAcceptedAt;
	private String clientIp;

	
	
	@ManyToOne
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@JsonManagedReference
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<OrderItem> itemlist=new ArrayList<>();
	
	@ManyToOne
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "adres_id")
    private Address teslimatAdresi;
	
	
	
	
	

}
