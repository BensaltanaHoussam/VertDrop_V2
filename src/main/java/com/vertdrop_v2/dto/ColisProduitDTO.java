package com.vertdrop_v2.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ColisProduitDTO {

    @NotNull(message = "Le produit est obligatoire.")
    private ProduitDTO produit;

    @NotNull(message = "La quantité est obligatoire.")
    @Min(value = 1, message = "La quantité doit être au moins 1.")
    private Integer quantite;

    @DecimalMin(value = "0.0", inclusive = true, message = "Le prix doit être positif ou nul.")
    private BigDecimal prix;

    private LocalDateTime dateAjout;
}
