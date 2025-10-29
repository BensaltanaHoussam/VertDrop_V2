package com.vertdrop_v2.dto;

import com.vertdrop_v2.entity.StatutColis;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HistoriqueLivraisonDTO {
    private Long id;
    private StatutColis statut;
    private LocalDateTime dateChangement;
    private String commentaire;



}