package com.vertdrop_v2.dto;

import com.vertdrop_v2.entity.StatutColis;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ColisDTO {
    private Long id;

    @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères.")
    private String description;

    @DecimalMin(value = "0.0", inclusive = true, message = "Le poids doit être positif ou nul.")
    @Digits(integer = 10, fraction = 2, message = "Le poids doit avoir au plus 10 chiffres et 2 décimales.")
    private BigDecimal poids;

    @NotNull(message = "Le statut du colis est obligatoire.")
    private StatutColis statut;

    @Min(value = 0, message = "La priorité doit être supérieure ou égale à 0.")
    private Integer priorite;

    @Size(max = 100, message = "La ville de destination ne doit pas dépasser 100 caractères.")
    private String villeDestination;

    @NotNull(message = "Le client expéditeur est obligatoire.")
    @Valid
    private ClientExpediteurDTO clientExpediteur;

    @NotNull(message = "Le destinataire est obligatoire.")
    @Valid
    private DestinataireDTO destinataire;

    @Valid
    private LivreurDTO livreur;

    @NotNull(message = "La zone est obligatoire.")
    @Valid
    private ZoneDTO zone;

    @Valid
    private List<HistoriqueLivraisonDTO> historiqueLivraisons;

    @Valid
    private List<ColisProduitDTO> colisProduits;
}
