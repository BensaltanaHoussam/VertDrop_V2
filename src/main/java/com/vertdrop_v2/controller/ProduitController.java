package com.vertdrop_v2.controller;

import com.vertdrop_v2.dto.ProduitDTO;
import com.vertdrop_v2.exception.NotFoundException;
import com.vertdrop_v2.service.ProduitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
@Tag(name = "Produits", description = "Endpoints de gestion des produits")
@Validated
public class ProduitController {

    private static final Logger log = LoggerFactory.getLogger(ProduitController.class);
    private final ProduitService produitService;

    public ProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }

    @GetMapping
    @Operation(summary = "Lister les produits", description = "Retourne tous les produits.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Liste"))
    public ResponseEntity<List<ProduitDTO>> getAllProduits() {
        log.info("GET /api/produits");
        return ResponseEntity.ok(produitService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un produit", description = "Retourne un produit par identifiant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trouvé"),
            @ApiResponse(responseCode = "404", description = "Introuvable")
    })
    public ResponseEntity<ProduitDTO> getProduitById(@PathVariable @Positive Long id) {
        log.info("GET /api/produits/{}", id);
        ProduitDTO dto = produitService.findById(id)
                .orElseThrow(() -> new NotFoundException("Produit introuvable id=" + id));
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @Operation(summary = "Créer un produit", description = "Crée un nouveau produit.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<ProduitDTO> createProduit(@Valid @RequestBody ProduitDTO produitDTO) {
        log.info("POST /api/produits");
        return new ResponseEntity<>(produitService.save(produitDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un produit", description = "Met à jour un produit existant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mis à jour"),
            @ApiResponse(responseCode = "404", description = "Introuvable")
    })
    public ResponseEntity<ProduitDTO> updateProduit(
            @PathVariable @Positive Long id,
            @Valid @RequestBody ProduitDTO produitDTO) {
        produitService.findById(id)
                .orElseThrow(() -> new NotFoundException("Produit introuvable id=" + id));
        produitDTO.setId(id);
        log.info("PUT /api/produits/{}", id);
        return ResponseEntity.ok(produitService.save(produitDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un produit", description = "Supprime un produit.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Supprimé"),
            @ApiResponse(responseCode = "404", description = "Introuvable")
    })
    public ResponseEntity<Void> deleteProduit(@PathVariable @Positive Long id) {
        produitService.findById(id)
                .orElseThrow(() -> new NotFoundException("Produit introuvable id=" + id));
        log.info("DELETE /api/produits/{}", id);
        produitService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
