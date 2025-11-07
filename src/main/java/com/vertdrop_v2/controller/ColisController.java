package com.vertdrop_v2.controller;

import com.vertdrop_v2.dto.ColisDTO;
import com.vertdrop_v2.dto.HistoriqueLivraisonDTO;
import com.vertdrop_v2.dto.UpdateStatusRequestDTO;
import com.vertdrop_v2.entity.StatutColis;
import com.vertdrop_v2.exception.NotFoundException;
import com.vertdrop_v2.service.ColisService;
import com.vertdrop_v2.service.LivreurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/colis")
@Tag(name = "Colis", description = "Endpoints de gestion des colis")
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
    @Operation(summary = "Lister les colis", description = "Recherche paginée des colis avec filtres statut, zone, mot clé.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Page de colis"))
    public ResponseEntity<Page<ColisDTO>> getAllColis(
            @Parameter(hidden = true) Pageable pageable,
            @RequestParam(required = false) StatutColis statut,
            @RequestParam(required = false) @Positive Long zoneId,
            @RequestParam(required = false) String keyword) {
        log.info("GET /api/colis statut={}, zoneId={}, keyword={}", statut, zoneId, keyword);
        return ResponseEntity.ok(colisService.findAll(pageable, statut, zoneId, keyword));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un colis", description = "Retourne un colis par son identifiant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Colis trouvé"),
            @ApiResponse(responseCode = "404", description = "Colis introuvable")
    })
    public ResponseEntity<ColisDTO> getColisById(@PathVariable @Positive Long id) {
        log.info("GET /api/colis/{}", id);
        ColisDTO dto = colisService.findById(id)
                .orElseThrow(() -> new NotFoundException("Colis introuvable id=" + id));
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @Operation(summary = "Créer un colis", description = "Crée un nouveau colis.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Colis créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<ColisDTO> createColis(@Valid @RequestBody ColisDTO colisDTO) {
        log.info("POST /api/colis");
        return new ResponseEntity<>(colisService.save(colisDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un colis", description = "Met à jour un colis existant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Colis mis à jour"),
            @ApiResponse(responseCode = "404", description = "Colis introuvable")
    })
    public ResponseEntity<ColisDTO> updateColis(
            @PathVariable @Positive Long id,
            @Valid @RequestBody ColisDTO colisDTO) {
        colisService.findById(id)
                .orElseThrow(() -> new NotFoundException("Colis introuvable id=" + id));
        colisDTO.setId(id);
        log.info("PUT /api/colis/{}", id);
        return ResponseEntity.ok(colisService.save(colisDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un colis", description = "Supprime un colis.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Supprimé"),
            @ApiResponse(responseCode = "404", description = "Colis introuvable")
    })
    public ResponseEntity<Void> deleteColis(@PathVariable @Positive Long id) {
        colisService.findById(id)
                .orElseThrow(() -> new NotFoundException("Colis introuvable id=" + id));
        log.info("DELETE /api/colis/{}", id);
        colisService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Changer le statut", description = "Met à jour le statut d\'un colis.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statut mis à jour"),
            @ApiResponse(responseCode = "404", description = "Colis introuvable")
    })
    public ResponseEntity<ColisDTO> updateColisStatus(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateStatusRequestDTO statusRequest) {
        log.info("PUT /api/colis/{}/status -> {}", id, statusRequest.getStatut());
        return ResponseEntity.ok(colisService.updateStatus(id, statusRequest.getStatut(), statusRequest.getCommentaire()));
    }

    @GetMapping("/{id}/colis")
    @Operation(summary = "Lister colis d\'un livreur", description = "Retourne les colis affectés à un livreur.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des colis"),
            @ApiResponse(responseCode = "404", description = "Livreur introuvable")
    })
    public ResponseEntity<List<ColisDTO>> getColisForLivreur(@PathVariable @Positive Long id) {
        livreurService.findById(id)
                .orElseThrow(() -> new NotFoundException("Livreur introuvable id=" + id));
        log.info("GET /api/colis/{}/colis", id);
        return ResponseEntity.ok(colisService.findByLivreurId(id));
    }

    @PutMapping("/{colisId}/assign-livreur/{livreurId}")
    @Operation(summary = "Assigner un livreur", description = "Assigne un livreur à un colis.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Colis mis à jour"),
            @ApiResponse(responseCode = "404", description = "Colis ou livreur introuvable")
    })
    public ResponseEntity<ColisDTO> assignLivreurToColis(
            @PathVariable("colisId") @Positive Long colisId,
            @PathVariable("livreurId") @Positive Long livreurId) {
        log.info("PUT /api/colis/{}/assign-livreur/{}", colisId, livreurId);
        return ResponseEntity.ok(colisService.assignLivreur(colisId, livreurId));
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Historique du colis", description = "Retourne l\'historique des livraisons pour un colis.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historique"),
            @ApiResponse(responseCode = "404", description = "Colis introuvable")
    })
    public ResponseEntity<List<HistoriqueLivraisonDTO>> getColisHistory(@PathVariable @Positive Long id) {
        colisService.findById(id)
                .orElseThrow(() -> new NotFoundException("Colis introuvable id=" + id));
        log.info("GET /api/colis/{}/history", id);
        return ResponseEntity.ok(colisService.findHistoryForColis(id));
    }
}
