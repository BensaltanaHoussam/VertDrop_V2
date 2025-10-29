package com.vertdrop_v2.dto;

import com.vertdrop_v2.entity.StatutColis;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ColisDTO {
    private Long id;
    private String description;
    private BigDecimal poids;
    private StatutColis statut;
    private Integer priorite;
    private String villeDestination;

    // Instead of entities, we hold DTOs
    private ClientExpediteurDTO clientExpediteur;
    private DestinataireDTO destinataire;
    private LivreurDTO livreur;
    private ZoneDTO zone;
    private List<HistoriqueLivraisonDTO> historiqueLivraisons;
    private List<ColisProduitDTO> colisProduits;

}