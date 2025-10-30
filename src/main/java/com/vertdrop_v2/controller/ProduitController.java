package com.vertdrop_v2.controller;

import com.vertdrop_v2.dto.ProduitDTO;
import com.vertdrop_v2.service.ProduitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
public class ProduitController {

    private final ProduitService produitService;

    public ProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }

    @GetMapping
    public ResponseEntity<List<ProduitDTO>> getAllProduits() {
        return ResponseEntity.ok(produitService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProduitDTO> getProduitById(@PathVariable Long id) {
        return produitService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProduitDTO> createProduit(@RequestBody ProduitDTO produitDTO) {
        ProduitDTO savedProduit = produitService.save(produitDTO);
        return new ResponseEntity<>(savedProduit, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProduitDTO> updateProduit(@PathVariable Long id, @RequestBody ProduitDTO produitDTO) {
        if (!produitService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        produitDTO.setId(id);
        ProduitDTO updatedProduit = produitService.save(produitDTO);
        return ResponseEntity.ok(updatedProduit);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduit(@PathVariable Long id) {
        if (!produitService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        produitService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}