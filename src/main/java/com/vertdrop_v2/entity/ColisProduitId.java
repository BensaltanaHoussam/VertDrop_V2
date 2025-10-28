package com.vertdrop_v2.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class ColisProduitId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "id_colis")
    private Long idColis;

    @Column(name = "id_produit")
    private Long idProduit;

}