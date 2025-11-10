package com.vertdrop_v2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ZoneDTO {
    private Long id;

    @NotBlank(message = "Le nom de la zone est obligatoire.")
    @Size(max = 255, message = "Le nom de la zone ne doit pas dépasser 255 caractères.")
    private String nom;

    @Size(max = 20, message = "Le code postal ne doit pas dépasser 20 caractères.")
    private String codePostal;
}
