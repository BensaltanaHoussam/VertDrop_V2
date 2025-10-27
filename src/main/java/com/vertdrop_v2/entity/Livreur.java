package com.vertdrop_v2.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "livreur")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Livreur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(length = 20)
    private String telephone;

    @Column(length = 50)
    private String vehicule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_assignee")
    private Zone zoneAssignee;

    @OneToMany(mappedBy = "livreur", fetch = FetchType.LAZY)
    private List<Colis> colis = new ArrayList<>();
}