package com.vertdrop_v2.controller;

import com.vertdrop_v2.dto.ColisDTO;
import com.vertdrop_v2.dto.LivreurDTO;
import com.vertdrop_v2.exception.NotFoundException;
import com.vertdrop_v2.service.ColisService;
import com.vertdrop_v2.service.LivreurService;
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
@RequestMapping("/api/livreurs")
@Validated
public class LivreurController {

    private static final Logger log = LoggerFactory.getLogger(LivreurController.class);

    private final LivreurService livreurService;
    private final ColisService colisService;

    public LivreurController(LivreurService livreurService , ColisService colisService) {
        this.livreurService = livreurService;
        this.colisService = colisService;
    }

    @GetMapping
    public ResponseEntity<List<LivreurDTO>> getAllLivreurs() {
        log.info("GET /api/livreurs");
        return ResponseEntity.ok(livreurService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivreurDTO> getLivreurById(
            @PathVariable @Positive(message = "L'identifiant doit être un entier positif") Long id) {
        log.info("GET /api/livreurs/{}", id);
        LivreurDTO dto = livreurService.findById(id)
                .orElseThrow(() -> new NotFoundException("Livreur introuvable id=" + id));
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<LivreurDTO> createLivreur(@Valid @RequestBody LivreurDTO livreurDTO) {
        log.info("POST /api/livreurs");
        LivreurDTO savedLivreur = livreurService.save(livreurDTO);
        return new ResponseEntity<>(savedLivreur, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LivreurDTO> updateLivreur(
            @PathVariable @Positive(message = "L'identifiant doit être un entier positif") Long id,
            @Valid @RequestBody LivreurDTO livreurDTO) {
        livreurService.findById(id)
                .orElseThrow(() -> new NotFoundException("Livreur introuvable id=" + id));
        livreurDTO.setId(id);
        log.info("PUT /api/livreurs/{}", id);
        LivreurDTO updatedLivreur = livreurService.save(livreurDTO);
        return ResponseEntity.ok(updatedLivreur);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLivreur(
            @PathVariable @Positive(message = "L'identifiant doit être un entier positif") Long id) {
        livreurService.findById(id)
                .orElseThrow(() -> new NotFoundException("Livreur introuvable id=" + id));
        log.info("DELETE /api/livreurs/{}", id);
        livreurService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/colis")
    public ResponseEntity<List<ColisDTO>> getColisForLivreur(
            @PathVariable @Positive(message = "L'identifiant doit être un entier positif") Long id) {
        livreurService.findById(id)
                .orElseThrow(() -> new NotFoundException("Livreur introuvable id=" + id));
        log.info("GET /api/livreurs/{}/colis", id);
        List<ColisDTO> colisList = colisService.findByLivreurId(id);
        return ResponseEntity.ok(colisList);
    }
}
