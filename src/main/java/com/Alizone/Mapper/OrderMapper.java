package com.Alizone.Mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.Alizone.Dto.*;
import com.Alizone.Entity.*;

@Component
public class OrderMapper {

    public DtoOrder toDto(Order order) {

        DtoOrder dto = new DtoOrder();
        dto.setOrderId(order.getId());
        dto.setTarih(order.getOlusturmatarihi());
        dto.setToplamTutar(order.getToplamtutar());
        dto.setStatus(order.getSiparisdurumu().name());
        
        

        dto.setTeslimatAdresi(toDtoAddress(order.getTeslimatAdresi()));

        dto.setItems(
            order.getItemlist()
                .stream()
                .map(this::toDtoItem)
                .collect(Collectors.toList())
        );

        return dto;
    }

    // ----------------------------
    // ADDRESS
    // ----------------------------
    private DtoAddress toDtoAddress(Address a) {
        DtoAddress dto = new DtoAddress();
        dto.setId(a.getId());
        dto.setAliciAdiSoyadi(a.getAliciAdiSoyadi());
        dto.setAdresSatir1(a.getAdresSatir1());
        dto.setAdresSatir2(a.getAdresSatir2());
        dto.setSehir(a.getSehir());
        dto.setIlce(a.getIlce());
        dto.setPostaKodu(a.getPostaKodu());
        dto.setUlke(a.getUlke());
        dto.setTelefon(a.getTelefon());

        // FATURA
        dto.setFaturaTipi(a.getFaturaTipi());
        dto.setFaturaAdiSoyadi(a.getFaturaAdiSoyadi());
        dto.setTcKimlikNo(a.getTcKimlikNo());
        dto.setFirmaAdi(a.getFirmaAdi());
        dto.setVergiNo(a.getVergiNo());
        dto.setVergiDairesi(a.getVergiDairesi());

        return dto;
    }

    // ----------------------------
    // ORDER ITEM
    // ----------------------------
    private DtoOrderItem toDtoItem(OrderItem item) {
        DtoOrderItem dto = new DtoOrderItem();
        dto.setÜrünismi(item.getProduct().getIsim());
        dto.setAdet(item.getAdet());
        dto.setFiyat(item.getToplamfiyat());
        dto.setIndirim(item.getIndirim());
        dto.setImageUrl(item.getImageurl());
        return dto;
    }
}
