package com.vertdrop_v2.controller;

import com.vertdrop_v2.dto.ColisDTO;
import com.vertdrop_v2.dto.HistoriqueLivraisonDTO;
import com.vertdrop_v2.dto.UpdateStatusRequestDTO;
import com.vertdrop_v2.entity.StatutColis;
import com.vertdrop_v2.exception.NotFoundException;
import com.vertdrop_v2.service.ColisService;
import com.vertdrop_v2.service.LivreurService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colis")
@Validated
public class ColisController {

    private static final Logger log = LoggerFactory.getLogger(ColisController.class);

    private final LivreurService livreurService;
    private final ColisService colisService;

    public ColisController(LivreurService livreurService, ColisService colisService) {
        this.colisService = colisService;
        this.livreurService = livreurService;
    }

    @GetMapping
    public ResponseEntity<Page<ColisDTO>> getAllColis(
            Pageable pageable,
            @RequestParam(required = false) StatutColis statut,
            @RequestParam(required = false) @Positive(message = "L'identifiant de zone doit être un entier positif") Long zoneId,
            @RequestParam(required = false) String keyword) {
        log.info("GET /api/colis statut={}, zoneId={}, keyword={}", statut, zoneId, keyword);
        Page<ColisDTO> colisPage = colisService.findAll(pageable, statut, zoneId, keyword);
        return ResponseEntity.ok(colisPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColisDTO> getColisById(
            @PathVariable @Positive(message = "L'identifiant doit être un entier positif") Long id) {
        log.info("GET /api/colis/{}", id);
        ColisDTO dto = colisService.findById(id)
                .orElseThrow(() -> new NotFoundException("Colis introuvable id=" + id));
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ColisDTO> createColis(@Valid @RequestBody ColisDTO colisDTO) {
        log.info("POST /api/colis");
        ColisDTO savedColis = colisService.save(colisDTO);
        return new ResponseEntity<>(savedColis, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ColisDTO> updateColis(
            @PathVariable @Positive(message = "L'identifiant doit être un entier positif") Long id,
            @Valid @RequestBody ColisDTO colisDTO) {
        colisService.findById(id)
                .orElseThrow(() -> new NotFoundException("Colis introuvable id=" + id));
        colisDTO.setId(id);
        log.info("PUT /api/colis/{}", id);
        ColisDTO updatedColis = colisService.save(colisDTO);
        return ResponseEntity.ok(updatedColis);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteColis(
            @PathVariable @Positive(message = "L'identifiant doit être un entier positif") Long id) {
        colisService.findById(id)
                .orElseThrow(() -> new NotFoundException("Colis introuvable id=" + id));
        log.info("DELETE /api/colis/{}", id);
        colisService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ColisDTO> updateColisStatus(
            @PathVariable @Positive(message = "L'identifiant doit être un entier positif") Long id,
            @Valid @RequestBody UpdateStatusRequestDTO statusRequest) {
        log.info("PUT /api/colis/{}/status -> {}", id, statusRequest.getStatut());
        ColisDTO updatedColis = colisService.updateStatus(
                id,
                statusRequest.getStatut(),
                statusRequest.getCommentaire()
        );
        return ResponseEntity.ok(updatedColis);
    }

    @GetMapping("/{id}/colis")
    public ResponseEntity<List<ColisDTO>> getColisForLivreur(
            @PathVariable @Positive(message = "L'identifiant doit être un entier positif") Long id) {
        livreurService.findById(id)
                .orElseThrow(() -> new NotFoundException("Livreur introuvable id=" + id));
        log.info("GET /api/colis/{}/colis", id);
        List<ColisDTO> colisList = colisService.findByLivreurId(id);
        return ResponseEntity.ok(colisList);
    }

    @PutMapping("/{colisId}/assign-livreur/{livreurId}")
    public ResponseEntity<ColisDTO> assignLivreurToColis(
            @PathVariable("colisId") @Positive(message = "L'identifiant doit être un entier positif") Long colisId,
            @PathVariable("livreurId") @Positive(message = "L'identifiant doit être un entier positif") Long livreurId) {
        log.info("PUT /api/colis/{}/assign-livreur/{}", colisId, livreurId);
        ColisDTO updatedColis = colisService.assignLivreur(colisId, livreurId);
        return ResponseEntity.ok(updatedColis);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<HistoriqueLivraisonDTO>> getColisHistory(
            @PathVariable @Positive(message = "L'identifiant doit être un entier positif") Long id) {
        colisService.findById(id)
                .orElseThrow(() -> new NotFoundException("Colis introuvable id=" + id));
        log.info("GET /api/colis/{}/history", id);
        List<HistoriqueLivraisonDTO> history = colisService.findHistoryForColis(id);
        return ResponseEntity.ok(history);
    }
}
