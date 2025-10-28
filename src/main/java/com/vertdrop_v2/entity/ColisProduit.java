package com.vertdrop_v2.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "colis_produit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ColisProduit {

    @EmbeddedId
    private ColisProduitId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idColis")
    @JoinColumn(name = "id_colis")
    private Colis colis;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idProduit")
    @JoinColumn(name = "id_produit")
    private Produit produit;

    @Column(nullable = false)
    private Integer quantite;

    @Column(precision = 10, scale = 2)
    private BigDecimal prix;

    @Column(name = "date_ajout")
    private LocalDateTime dateAjout;
}