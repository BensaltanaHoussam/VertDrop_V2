package com.vertdrop_v2.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "zone")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(name = "code_postal", length = 20)
    private String codePostal;

    @OneToMany(mappedBy = "zone", fetch = FetchType.LAZY)
    private List<Colis> colis = new ArrayList<>();

    @OneToMany(mappedBy = "zoneAssignee", fetch = FetchType.LAZY)
    private List<Livreur> livreurs = new ArrayList<>();
}