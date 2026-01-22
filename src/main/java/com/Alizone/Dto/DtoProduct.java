package com.Alizone.Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale.Category;

import com.Alizone.Entity.Embeddable.Boyutlar;
import com.Alizone.Entity.Embeddable.DigerOzellikler;
import com.Alizone.Entity.Embeddable.Enerji;
import com.Alizone.Entity.Embeddable.Gaz;
import com.Alizone.Entity.Embeddable.Kapasite;
import com.Alizone.Entity.Embeddable.Sertifikalar;
import com.Alizone.Enum.CATEGORY;
import com.Alizone.Enum.Marka;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.PrePersist;
import lombok.Data;

@Data
public class DtoProduct {
	
	private Long id;
	private Marka marka;
	private String model;
	private String isim;
	private CATEGORY kategori;
	private BigDecimal fiyat;
	private String renk;
    private Boolean inverter;
    private Integer garantiAy;
    private String stokDurumu; 
    private List<String> onemliOzellikler;
    private List<String> notlar;
    private List<String> resimler;
    private Kapasite kapasite;
    private Enerji enerji;
    private Gaz refrigerant;
    private DigerOzellikler digerOzellikler;
    private Boyutlar boyutlar;
    private Sertifikalar sertifikalar;
    private LocalDateTime eklenmeTarihi;
    private Integer btu;
    private Integer stokadeti;
    private boolean teklifilesatilir;
    
}
	


