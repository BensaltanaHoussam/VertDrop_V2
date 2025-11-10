package com.vertdrop_v2.dto;

import com.vertdrop_v2.entity.StatutColis;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ColisCreateRequestDTO {

    @Size(max = 500)
    private String description;

    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal poids;

    @NotNull
    @Pattern(regexp = "^(|CREE|COLLECTE|EN_STOCK|EN_TRANSIT|LIVRE)$", message = "le statut doit être l'une des valeurs suivantes : CREE, COLLECTE, EN_STOCK, EN_TRANSIT, LIVRE")
    private String statut;

    @Min(0)
    private Integer priorite;

    @Size(max = 100)
    private String villeDestination;

    @NotNull
    private Long clientExpediteurId;

    @NotNull
    private Long destinataireId;

    private Long livreurId; // optionnel

    @NotNull
    private Long zoneId;

    @Valid
    private List<ColisProduitItemRequest> produits;

    @Data
    public static class ColisProduitItemRequest {
        @NotNull
        private Long produitId;
        @NotNull
        @Min(1)
        private Integer quantite;
        @DecimalMin(value = "0.0", inclusive = true)
        private BigDecimal prix; // optionnel (utilise prix du produit si null)
    }
}
