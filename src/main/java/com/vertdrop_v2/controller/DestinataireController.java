package com.vertdrop_v2.controller;

import com.vertdrop_v2.dto.DestinataireDTO;
import com.vertdrop_v2.exception.NotFoundException;
import com.vertdrop_v2.service.DestinataireService;
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
@RequestMapping("/api/destinataires")
@Tag(name = "Destinataires", description = "Endpoints de gestion des destinataires")
@Validated
public class DestinataireController {

    private static final Logger log = LoggerFactory.getLogger(DestinataireController.class);
    private final DestinataireService destinataireService;

    public DestinataireController(DestinataireService destinataireService) {
        this.destinataireService = destinataireService;
    }

    @GetMapping
    @Operation(summary = "Lister les destinataires", description = "Retourne tous les destinataires.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Liste"))
    public ResponseEntity<List<DestinataireDTO>> getAllDestinataires() {
        log.info("GET /api/destinataires");
        return ResponseEntity.ok(destinataireService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un destinataire", description = "Retourne un destinataire par identifiant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trouvé"),
            @ApiResponse(responseCode = "404", description = "Introuvable")
    })
    public ResponseEntity<DestinataireDTO> getDestinataireById(@PathVariable @Positive Long id) {
        log.info("GET /api/destinataires/{}", id);
        DestinataireDTO dto = destinataireService.findById(id)
                .orElseThrow(() -> new NotFoundException("Destinataire introuvable id=" + id));
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @Operation(summary = "Créer un destinataire", description = "Crée un nouveau destinataire.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<DestinataireDTO> createDestinataire(@Valid @RequestBody DestinataireDTO destinataireDTO) {
        log.info("POST /api/destinataires");
        return new ResponseEntity<>(destinataireService.save(destinataireDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un destinataire", description = "Met à jour un destinataire.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mis à jour"),
            @ApiResponse(responseCode = "404", description = "Introuvable")
    })
    public ResponseEntity<DestinataireDTO> updateDestinataire(
            @PathVariable @Positive Long id,
            @Valid @RequestBody DestinataireDTO destinataireDTO) {
        destinataireService.findById(id)
                .orElseThrow(() -> new NotFoundException("Destinataire introuvable id=" + id));
        destinataireDTO.setId(id);
        log.info("PUT /api/destinataires/{}", id);
        return ResponseEntity.ok(destinataireService.save(destinataireDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un destinataire", description = "Supprime un destinataire.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Supprimé"),
            @ApiResponse(responseCode = "404", description = "Introuvable")
    })
    public ResponseEntity<Void> deleteDestinataire(@PathVariable @Positive Long id) {
        destinataireService.findById(id)
                .orElseThrow(() -> new NotFoundException("Destinataire introuvable id=" + id));
        log.info("DELETE /api/destinataires/{}", id);
        destinataireService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
