package com.vertdrop_v2.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProduitDTO {
    private Long id;
    private String nom;
    private String categorie;
    private BigDecimal poids;
    private BigDecimal prix;
    private Long clientExpediteurId;
}