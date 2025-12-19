package com.vertdrop_v2.controller;

import com.vertdrop_v2.dto.ColisCreateRequestDTO;
import com.vertdrop_v2.dto.ColisDTO;
import com.vertdrop_v2.dto. HistoriqueLivraisonDTO;
import com.vertdrop_v2.dto.UpdateStatusRequestDTO;
import com.vertdrop_v2.entity.StatutColis;
import com. vertdrop_v2.exception. NotFoundException;
import com.vertdrop_v2.service.AuthService;
import com.vertdrop_v2.service.ColisService;
import com.vertdrop_v2.service. LivreurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger. v3.oas.annotations. responses.ApiResponse;
import io.swagger. v3.oas.annotations. Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints. Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain. Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind. annotation.*;

import java.util. List;

@RestController
@RequestMapping("/api/colis")
@Tag(name = "Colis", description = "Endpoints de gestion des colis")
@Validated
public class ColisController {

    private static final Logger log = LoggerFactory.getLogger(ColisController.class);
    private final LivreurService livreurService;
    private final ColisService colisService;
    private final AuthService authService;

    public ColisController(LivreurService livreurService, ColisService colisService, AuthService authService) {
        this.colisService = colisService;
        this.livreurService = livreurService;
        this.authService = authService;
    }

    /**
     * GET /api/colis - Filtered by role
     * MANAGER: sees all
     * LIVREUR: sees only his colis
     * CLIENT: sees only his colis
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'LIVREUR', 'CLIENT')")
    @Operation(summary = "Lister les colis", description = "Recherche paginée des colis avec filtres statut, zone, mot clé.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Page de colis"))
    public ResponseEntity<Page<ColisDTO>> getAllColis(
            @Parameter(hidden = true) Pageable pageable,
            @RequestParam(required = false) StatutColis statut,
            @RequestParam(required = false) @Positive Long zoneId,
            @RequestParam(required = false) String keyword) {
        log.info("GET /api/colis statut={}, zoneId={}, keyword={}", statut, zoneId, keyword);

        // Filter by role
        if (authService.hasRole("ROLE_MANAGER")) {
            // Manager sees everything
            return ResponseEntity.ok(colisService.findAll(pageable, statut, zoneId, keyword));
        } else if (authService.hasRole("ROLE_LIVREUR")) {
            // Livreur sees only his colis
            Long livreurId = authService. getCurrentLivreur()
                    .orElseThrow(() -> new RuntimeException("Livreur not found for current user"))
                    .getId();
            return ResponseEntity.ok(colisService.findAllForLivreur(livreurId, pageable, statut, zoneId, keyword));
        } else if (authService.hasRole("ROLE_CLIENT")) {
            // Client sees only his colis
            Long clientId = authService.getCurrentClient()
                    .orElseThrow(() -> new RuntimeException("Client not found for current user"))
                    .getId();
            return ResponseEntity.ok(colisService. findAllForClient(clientId, pageable, statut, zoneId, keyword));
        }

        return ResponseEntity. status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'LIVREUR', 'CLIENT')")
    @Operation(summary = "Récupérer un colis", description = "Retourne un colis par son identifiant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Colis trouvé"),
            @ApiResponse(responseCode = "404", description = "Colis introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<ColisDTO> getColisById(@PathVariable @Positive Long id) {
        log.info("GET /api/colis/{}", id);
        ColisDTO dto = colisService.findById(id)
                .orElseThrow(() -> new NotFoundException("Colis introuvable id=" + id));

        // Check access rights
        if (authService.hasRole("ROLE_MANAGER")) {
            return ResponseEntity.ok(dto);
        } else if (authService. hasRole("ROLE_LIVREUR")) {
            Long livreurId = authService.getCurrentLivreur()
                    .orElseThrow(() -> new RuntimeException("Livreur not found"))
                    .getId();
            if (dto.getLivreur() != null && dto.getLivreur().getId().equals(livreurId)) {
                return ResponseEntity.ok(dto);
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else if (authService.hasRole("ROLE_CLIENT")) {
            Long clientId = authService.getCurrentClient()
                    . orElseThrow(() -> new RuntimeException("Client not found"))
                    .getId();
            if (dto.getClientExpediteur() != null && dto.getClientExpediteur().getId().equals(clientId)) {
                return ResponseEntity.ok(dto);
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity. status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'CLIENT')")
    @Operation(summary = "Créer un colis", description = "Crée un colis (références par ID).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Colis créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<ColisDTO> createColis(@Valid @RequestBody ColisCreateRequestDTO req) {
        log.info("POST /api/colis (request DTO)");

        // If client is creating, force it to be HIS colis
        if (authService.hasRole("ROLE_CLIENT")) {
            Long clientId = authService.getCurrentClient()
                    .orElseThrow(() -> new RuntimeException("Client not found"))
                    .getId();
            req.setClientExpediteurId(clientId); // Force client to be the current user
        }

        ColisDTO dto = colisService.createFromRequest(req);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'CLIENT')")
    @Operation(summary = "Mettre à jour un colis", description = "Met à jour un colis existant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Colis mis à jour"),
            @ApiResponse(responseCode = "404", description = "Colis introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<ColisDTO> updateColis(
            @PathVariable @Positive Long id,
            @Valid @RequestBody ColisDTO colisDTO) {

        ColisDTO existing = colisService.findById(id)
                .orElseThrow(() -> new NotFoundException("Colis introuvable id=" + id));

        // Check access
        if (authService.hasRole("ROLE_CLIENT")) {
            Long clientId = authService.getCurrentClient()
                    .orElseThrow(() -> new RuntimeException("Client not found"))
                    .getId();
            if (existing.getClientExpediteur() == null || !existing.getClientExpediteur().getId().equals(clientId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        colisDTO.setId(id);
        log.info("PUT /api/colis/{}", id);
        return ResponseEntity.ok(colisService.save(colisDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Supprimer un colis", description = "Supprime un colis (MANAGER uniquement).")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Supprimé"),
            @ApiResponse(responseCode = "404", description = "Colis introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Void> deleteColis(@PathVariable @Positive Long id) {
        colisService.findById(id)
                .orElseThrow(() -> new NotFoundException("Colis introuvable id=" + id));
        log.info("DELETE /api/colis/{}", id);
        colisService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('MANAGER', 'LIVREUR')")
    @Operation(summary = "Changer le statut", description = "Met à jour le statut d'un colis.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statut mis à jour"),
            @ApiResponse(responseCode = "404", description = "Colis introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<ColisDTO> updateColisStatus(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateStatusRequestDTO statusRequest) {
        log.info("PUT /api/colis/{}/status -> {}", id, statusRequest.getStatut());

        // Check if livreur can update this colis
        if (authService.hasRole("ROLE_LIVREUR")) {
            ColisDTO colis = colisService.findById(id)
                    .orElseThrow(() -> new NotFoundException("Colis introuvable id=" + id));
            Long livreurId = authService. getCurrentLivreur()
                    .orElseThrow(() -> new RuntimeException("Livreur not found"))
                    . getId();
            if (colis.getLivreur() == null || !colis.getLivreur().getId().equals(livreurId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        return ResponseEntity.ok(colisService.updateStatus(id, StatutColis.valueOf(statusRequest.getStatut().toUpperCase()), statusRequest.getCommentaire()));
    }

    @GetMapping("/{id}/colis")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Lister colis d'un livreur", description = "Retourne les colis affectés à un livreur.")
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
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Assigner un livreur", description = "Assigne un livreur à un colis (MANAGER uniquement).")
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
    @PreAuthorize("hasAnyRole('MANAGER', 'LIVREUR', 'CLIENT')")
    @Operation(summary = "Historique du colis", description = "Retourne l'historique des livraisons pour un colis.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historique"),
            @ApiResponse(responseCode = "404", description = "Colis introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<HistoriqueLivraisonDTO>> getColisHistory(@PathVariable @Positive Long id) {
        ColisDTO colis = colisService.findById(id)
                .orElseThrow(() -> new NotFoundException("Colis introuvable id=" + id));

        // Check access
        if (authService.hasRole("ROLE_LIVREUR")) {
            Long livreurId = authService.getCurrentLivreur()
                    .orElseThrow(() -> new RuntimeException("Livreur not found"))
                    .getId();
            if (colis.getLivreur() == null || !colis.getLivreur().getId().equals(livreurId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else if (authService.hasRole("ROLE_CLIENT")) {
            Long clientId = authService. getCurrentClient()
                    .orElseThrow(() -> new RuntimeException("Client not found"))
                    .getId();
            if (colis.getClientExpediteur() == null || !colis.getClientExpediteur().getId().equals(clientId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        log.info("GET /api/colis/{}/history", id);
        return ResponseEntity.ok(colisService.findHistoryForColis(id));
    }


    @GetMapping("/mes-colis")
    @PreAuthorize("hasAnyRole('LIVREUR', 'CLIENT')")
    @Operation(summary = "Mes colis", description = "Retourne les colis de l'utilisateur connecté.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Liste des colis"))
    public ResponseEntity<List<ColisDTO>> getMesColis() {
        log.info("GET /api/colis/mes-colis");

        if (authService.hasRole("ROLE_LIVREUR")) {
            Long livreurId = authService.getCurrentLivreur()
                    .orElseThrow(() -> new RuntimeException("Livreur not found"))
                    .getId();
            return ResponseEntity.ok(colisService.findByLivreurId(livreurId));
        } else if (authService.hasRole("ROLE_CLIENT")) {
            Long clientId = authService.getCurrentClient()
                    . orElseThrow(() -> new RuntimeException("Client not found"))
                    .getId();
            return ResponseEntity.ok(colisService.findByClientId(clientId));
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}