package com.Alizone.Entity.Embeddable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sertifikalar {
    private String erpEnerjiEtiketi;
    private Boolean fgazIceriyor;
    private Boolean hermetikMuhur;
}
