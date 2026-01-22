package com.Alizone.Dto;

import lombok.Data;

@Data
public class DtoAddress {
    private Long id;
    private String aliciAdiSoyadi;
    private String adresSatir1;
    private String adresSatir2;
    private String sehir;
    private String ilce;
    private String postaKodu;
    private String ulke;
    private String telefon;
    private String faturaTipi;
    private String faturaAdiSoyadi;
    private String tcKimlikNo;
    private String firmaAdi;
    private String vergiNo;
    private String vergiDairesi;
}
