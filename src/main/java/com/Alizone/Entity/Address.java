package com.Alizone.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Data
@Table(name = "adresler",schema = "alizone")
public class Address {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String aliciAdiSoyadi;
    private String adresSatir1;
    private String adresSatir2;
    private String sehir;
    private String ilce;
    private String postaKodu;
    private String ulke;
    private String telefon;
    
    
 // FATURA (OPSİYONEL)
    private String faturaTipi; // BIREYSEL / KURUMSAL
    private String faturaAdiSoyadi;
    private String tcKimlikNo;
    private String firmaAdi;
    private String vergiNo;
    private String vergiDairesi;
   
    
    
    private String kargoTakipNo;
    private String gonderimTarihi;
    private String teslimTarihi;
    
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
