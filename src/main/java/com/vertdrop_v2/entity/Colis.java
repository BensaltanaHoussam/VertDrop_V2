package com.vertdrop_v2.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "colis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Colis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal poids;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutColis statut;

    private Integer priorite;

    @Column(name = "ville_destination", length = 100)
    private String villeDestination;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client")
    private ClientExpediteur clientExpediteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_destinataire")
    private Destinataire destinataire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_livreur")
    private Livreur livreur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_zone")
    private Zone zone;

    @OneToMany(mappedBy = "colis", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<HistoriqueLivraison> historiqueLivraisons = new ArrayList<>();

    @OneToMany(mappedBy = "colis", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ColisProduit> colisProduits = new ArrayList<>();
}