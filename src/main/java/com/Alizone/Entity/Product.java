package com.Alizone.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


import com.Alizone.Entity.Embeddable.Boyutlar;
import com.Alizone.Entity.Embeddable.DigerOzellikler;
import com.Alizone.Entity.Embeddable.Enerji;
import com.Alizone.Entity.Embeddable.Gaz;
import com.Alizone.Entity.Embeddable.Kapasite;
import com.Alizone.Entity.Embeddable.Sertifikalar;
import com.Alizone.Enum.CATEGORY;
import com.Alizone.Enum.Marka;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "urunler", schema = "alizone")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private Marka marka;
    
    private String model;
    
    private String isim;
    
    @Enumerated(EnumType.STRING)
    private CATEGORY kategori;

    private BigDecimal fiyat;

    private String renk;
    private Boolean inverter;
    private Integer garantiAy;

    private String stokDurumu;
    private Integer stokAdeti;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> onemliOzellikler;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> notlar;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> resimler;

    @Embedded
    private Kapasite kapasite;

    @Embedded
    private Enerji enerji;

    @Embedded
    private Gaz refrigerant;

    @Embedded
    private DigerOzellikler digerOzellikler;

    @Embedded
    private Boyutlar boyutlar;

    @Embedded
    private Sertifikalar sertifikalar;
    
    @Column(name = "teklifilesatilir")
    private boolean teklifilesatilir=true;
    
    @Column(nullable = false)
    @Version
    private Long version;
    
    @Column(nullable = false)
    private Integer reservedStock = 0;
    
    @Column(nullable = false)
    private boolean aktif = true;
    
    
    Integer btu;


    @PrePersist
    protected void onCreate() {
        if (eklenmeTarihi == null) eklenmeTarihi = LocalDateTime.now();
    }

    private LocalDateTime eklenmeTarihi;
}
