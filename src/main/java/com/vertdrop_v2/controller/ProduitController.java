package com.vertdrop_v2.controller;

import com.vertdrop_v2.dto.ProduitDTO;
import com.vertdrop_v2.exception.NotFoundException;
import com.vertdrop_v2.service.ProduitService;
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
@Validated
public class ProduitController {

    private static final Logger log = LoggerFactory.getLogger(ProduitController.class);
    private final ProduitService produitService;

    public ProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }

    @GetMapping
    public ResponseEntity<List<ProduitDTO>> getAllProduits() {
        log.info("GET /api/produits");
        return ResponseEntity.ok(produitService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProduitDTO> getProduitById(
            @PathVariable @Positive(message = "L'identifiant doit être un entier positif") Long id) {
        log.info("GET /api/produits/{}", id);
        ProduitDTO dto = produitService.findById(id)
                .orElseThrow(() -> new NotFoundException("Produit introuvable id=" + id));
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ProduitDTO> createProduit(@Valid @RequestBody ProduitDTO produitDTO) {
        log.info("POST /api/produits");
        ProduitDTO savedProduit = produitService.save(produitDTO);
        return new ResponseEntity<>(savedProduit, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProduitDTO> updateProduit(
            @PathVariable @Positive(message = "L'identifiant doit être un entier positif") Long id,
            @Valid @RequestBody ProduitDTO produitDTO) {
        produitService.findById(id)
                .orElseThrow(() -> new NotFoundException("Produit introuvable id=" + id));
        produitDTO.setId(id);
        log.info("PUT /api/produits/{}", id);
        ProduitDTO updatedProduit = produitService.save(produitDTO);
        return ResponseEntity.ok(updatedProduit);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduit(
            @PathVariable @Positive(message = "L'identifiant doit être un entier positif") Long id) {
        produitService.findById(id)
                .orElseThrow(() -> new NotFoundException("Produit introuvable id=" + id));
        log.info("DELETE /api/produits/{}", id);
        produitService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
