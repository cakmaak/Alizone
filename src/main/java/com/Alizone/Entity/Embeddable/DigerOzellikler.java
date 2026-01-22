package com.Alizone.Entity.Embeddable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DigerOzellikler {

    @Embedded
    private HavaAkisi havaAkisi;

    @Embedded
    private SesSeviyesi sesSeviyesi;

    @Embedded
    private CalismaSicakligi calismaSicakligi;

    @Embedded
    private Tasarim tasarim;
}

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class HavaAkisi {
    private Integer icHavaM3h;
    private Integer disHavaM3h;
}

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class SesSeviyesi {
    private Integer icSogutmaDb;
    private Integer disSogutmaDb;
    private Integer icIsitmaDb;
    private Integer disIsitmaDb;
}

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class CalismaSicakligi {
    private Integer sogutmaMin;
    private Integer sogutmaMax;
    private Integer isitmaMin;
    private Integer isitmaMax;
}

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Tasarim {
    private Boolean cikartilabilirPanel;
    private Boolean ciftDrenajBaglantisi;
}
