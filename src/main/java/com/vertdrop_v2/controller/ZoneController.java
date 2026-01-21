package com.vertdrop_v2.controller;

import com.vertdrop_v2.dto.ZoneDTO;
import com.vertdrop_v2.exception.NotFoundException;
import com.vertdrop_v2.service.ZoneService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
@Tag(name = "Zones", description = "Endpoints de gestion des zones")
@Validated
@PreAuthorize("hasAnyRole('MANAGER', 'CLIENT')")
public class ZoneController {

        private static final Logger log = LoggerFactory.getLogger(ZoneController.class);
        private final ZoneService zoneService;

        public ZoneController(ZoneService zoneService) {
                this.zoneService = zoneService;
        }

        @GetMapping
        @Operation(summary = "Lister les zones", description = "Retourne toutes les zones (MANAGER uniquement).")
        @ApiResponses(@ApiResponse(responseCode = "200", description = "Liste"))
        public ResponseEntity<List<ZoneDTO>> getAllZones() {
                log.info("GET /api/zones");
                return ResponseEntity.ok(zoneService.findAll());
        }

        @GetMapping("/{id}")
        @Operation(summary = "Récupérer une zone", description = "Retourne une zone par identifiant.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Trouvée"),
                        @ApiResponse(responseCode = "404", description = "Introuvable")
        })
        public ResponseEntity<ZoneDTO> getZoneById(@PathVariable @Positive Long id) {
                log.info("GET /api/zones/{}", id);
                ZoneDTO dto = zoneService.findById(id)
                                .orElseThrow(() -> new NotFoundException("Zone introuvable id=" + id));
                return ResponseEntity.ok(dto);
        }

        @PostMapping
        @Operation(summary = "Créer une zone", description = "Crée une nouvelle zone.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Créée"),
                        @ApiResponse(responseCode = "400", description = "Données invalides")
        })
        public ResponseEntity<ZoneDTO> createZone(@Valid @RequestBody ZoneDTO zoneDTO) {
                log.info("POST /api/zones");
                return new ResponseEntity<>(zoneService.save(zoneDTO), HttpStatus.CREATED);
        }

        @PutMapping("/{id}")
        @Operation(summary = "Mettre à jour une zone", description = "Met à jour une zone existante.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Mise à jour"),
                        @ApiResponse(responseCode = "404", description = "Introuvable")
        })
        public ResponseEntity<ZoneDTO> updateZone(
                        @PathVariable @Positive Long id,
                        @Valid @RequestBody ZoneDTO zoneDTO) {
                zoneService.findById(id)
                                .orElseThrow(() -> new NotFoundException("Zone introuvable id=" + id));
                zoneDTO.setId(id);
                log.info("PUT /api/zones/{}", id);
                return ResponseEntity.ok(zoneService.save(zoneDTO));
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Supprimer une zone", description = "Supprime une zone.")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Supprimée"),
                        @ApiResponse(responseCode = "404", description = "Introuvable")
        })
        public ResponseEntity<Void> deleteZone(@PathVariable @Positive Long id) {
                zoneService.findById(id)
                                .orElseThrow(() -> new NotFoundException("Zone introuvable id=" + id));
                log.info("DELETE /api/zones/{}", id);
                zoneService.deleteById(id);
                return ResponseEntity.noContent().build();
        }
}