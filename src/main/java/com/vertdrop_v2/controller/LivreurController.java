package com.vertdrop_v2.controller;

import com.vertdrop_v2.dto.ColisDTO;
import com.vertdrop_v2.dto. LivreurDTO;
import com.vertdrop_v2.exception.NotFoundException;
import com.vertdrop_v2.service.AuthService;
import com.vertdrop_v2.service.ColisService;
import com.vertdrop_v2.service. LivreurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses. ApiResponses;
import io. swagger.v3.oas. annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints. Positive;
import org.slf4j.Logger;
import org. slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access. prepost.PreAuthorize;
import org.springframework.validation. annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livreurs")
@Tag(name = "Livreurs", description = "Endpoints de gestion des livreurs")
@Validated
public class LivreurController {

    private static final Logger log = LoggerFactory.getLogger(LivreurController.class);
    private final LivreurService livreurService;
    private final ColisService colisService;
    private final AuthService authService;

    public LivreurController(LivreurService livreurService, ColisService colisService, AuthService authService) {
        this.livreurService = livreurService;
        this.colisService = colisService;
        this. authService = authService;
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Lister les livreurs", description = "Retourne tous les livreurs (MANAGER uniquement).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<LivreurDTO>> getAllLivreurs() {
        log.info("GET /api/livreurs");
        return ResponseEntity.ok(livreurService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'LIVREUR')")
    @Operation(summary = "Récupérer un livreur", description = "Retourne un livreur par identifiant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trouvé"),
            @ApiResponse(responseCode = "404", description = "Introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<LivreurDTO> getLivreurById(@PathVariable @Positive Long id) {
        log.info("GET /api/livreurs/{}", id);

        // Livreur can only see himself
        if (authService.hasRole("ROLE_LIVREUR")) {
            Long currentLivreurId = authService.getCurrentLivreur()
                    .orElseThrow(() -> new RuntimeException("Livreur not found"))
                    .getId();
            if (!id.equals(currentLivreurId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        LivreurDTO dto = livreurService.findById(id)
                .orElseThrow(() -> new NotFoundException("Livreur introuvable id=" + id));
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Créer un livreur", description = "Crée un nouveau livreur (MANAGER uniquement).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<LivreurDTO> createLivreur(@Valid @RequestBody LivreurDTO livreurDTO) {
        log.info("POST /api/livreurs");
        return new ResponseEntity<>(livreurService.save(livreurDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'LIVREUR')")
    @Operation(summary = "Mettre à jour un livreur", description = "Met à jour un livreur existant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mis à jour"),
            @ApiResponse(responseCode = "404", description = "Introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<LivreurDTO> updateLivreur(
            @PathVariable @Positive Long id,
            @Valid @RequestBody LivreurDTO livreurDTO) {

        // Livreur can only update himself
        if (authService.hasRole("ROLE_LIVREUR")) {
            Long currentLivreurId = authService.getCurrentLivreur()
                    .orElseThrow(() -> new RuntimeException("Livreur not found"))
                    .getId();
            if (!id.equals(currentLivreurId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        livreurService.findById(id)
                .orElseThrow(() -> new NotFoundException("Livreur introuvable id=" + id));
        livreurDTO.setId(id);
        log.info("PUT /api/livreurs/{}", id);
        return ResponseEntity.ok(livreurService.save(livreurDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Supprimer un livreur", description = "Supprime un livreur (MANAGER uniquement).")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Supprimé"),
            @ApiResponse(responseCode = "404", description = "Introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Void> deleteLivreur(@PathVariable @Positive Long id) {
        livreurService.findById(id)
                .orElseThrow(() -> new NotFoundException("Livreur introuvable id=" + id));
        log.info("DELETE /api/livreurs/{}", id);
        livreurService.deleteById(id);
        return ResponseEntity. noContent().build();
    }

    @GetMapping("/{id}/colis")
    @PreAuthorize("hasAnyRole('MANAGER', 'LIVREUR')")
    @Operation(summary = "Lister colis d'un livreur", description = "Retourne les colis assignés à un livreur.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste"),
            @ApiResponse(responseCode = "404", description = "Livreur introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<ColisDTO>> getColisForLivreur(@PathVariable @Positive Long id) {
        // Livreur can only see his own colis
        if (authService.hasRole("ROLE_LIVREUR")) {
            Long currentLivreurId = authService.getCurrentLivreur()
                    .orElseThrow(() -> new RuntimeException("Livreur not found"))
                    .getId();
            if (!id.equals(currentLivreurId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        livreurService.findById(id)
                .orElseThrow(() -> new NotFoundException("Livreur introuvable id=" + id));
        log.info("GET /api/livreurs/{}/colis", id);
        return ResponseEntity.ok(colisService.findByLivreurId(id));
    }




    @GetMapping("/me")
    @PreAuthorize("hasRole('LIVREUR')")
    @Operation(summary = "Mon profil", description = "Retourne le profil du livreur connecté.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Profil récupéré"))
    public ResponseEntity<LivreurDTO> getMyProfile() {
        log.info("GET /api/livreurs/me");
        LivreurDTO livreur = authService.getCurrentLivreur()
                .map(l -> livreurService.findById(l.getId()).orElseThrow())
                .orElseThrow(() -> new RuntimeException("Livreur not found for current user"));
        return ResponseEntity.ok(livreur);
    }



    @GetMapping("/me/colis")
    @PreAuthorize("hasRole('LIVREUR')")
    @Operation(summary = "Mes colis", description = "Retourne les colis assignés au livreur connecté.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Liste des colis"))
    public ResponseEntity<List<ColisDTO>> getMesColis() {
        log.info("GET /api/livreurs/me/colis");
        Long livreurId = authService.getCurrentLivreur()
                .orElseThrow(() -> new RuntimeException("Livreur not found"))
                .getId();
        return ResponseEntity.ok(colisService.findByLivreurId(livreurId));
    }
}