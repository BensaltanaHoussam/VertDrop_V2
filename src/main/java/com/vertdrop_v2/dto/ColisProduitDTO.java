package com.vertdrop_v2.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ColisProduitDTO {

    private ProduitDTO produit;
    private Integer quantite;
    private BigDecimal prix;
    private LocalDateTime dateAjout;
}