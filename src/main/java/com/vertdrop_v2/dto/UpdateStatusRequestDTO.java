package com.vertdrop_v2.dto;

import com.vertdrop_v2.entity.StatutColis;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateStatusRequestDTO {

    @NotNull(message = "Le statut est obligatoire.")
    @Pattern(regexp = "^(|CREE|COLLECTE|EN_STOCK|EN_TRANSIT|LIVRE)$", message = "le statut doit être l'une des valeurs suivantes : CREE, COLLECTE, EN_STOCK, EN_TRANSIT, LIVRE")
    private String statut;

    @Size(max = 1000, message = "Le commentaire ne doit pas dépasser 1000 caractères.")
    private String commentaire;
}
