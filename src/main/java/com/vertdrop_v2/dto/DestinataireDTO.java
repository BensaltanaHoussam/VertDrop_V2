package com.vertdrop_v2.dto;

import lombok.Data;

@Data
public class DestinataireDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
}