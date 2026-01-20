package com.vertdrop_v2.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LivreurDTO {
    private Long id;

    @NotBlank(message = "Le nom est obligatoire.")
    @Size(max = 255, message = "Le nom ne doit pas dépasser 255 caractères.")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire.")
    @Size(max = 255, message = "Le prénom ne doit pas dépasser 255 caractères.")
    private String prenom;

    private String email;

    @Size(max = 20, message = "Le téléphone ne doit pas dépasser 20 caractères.")
    private String telephone;

    @Size(max = 50, message = "Le véhicule ne doit pas dépasser 50 caractères.")
    private String vehicule;

    @Positive(message = "L'identifiant de la zone assignée doit être un entier positif.")
    private Long zoneAssignee;

    private String password;
}
