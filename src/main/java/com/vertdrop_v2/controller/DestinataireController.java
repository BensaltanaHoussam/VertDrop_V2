package com.vertdrop_v2.controller;

import com.vertdrop_v2.dto.DestinataireDTO;
import com.vertdrop_v2.exception.NotFoundException;
import com.vertdrop_v2.service.DestinataireService;
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
@Validated
public class DestinataireController {

    private static final Logger log = LoggerFactory.getLogger(DestinataireController.class);
    private final DestinataireService destinataireService;

    public DestinataireController(DestinataireService destinataireService) {
        this.destinataireService = destinataireService;
    }

    @GetMapping
    public ResponseEntity<List<DestinataireDTO>> getAllDestinataires() {
        log.info("GET /api/destinataires");
        return ResponseEntity.ok(destinataireService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DestinataireDTO> getDestinataireById(
            @PathVariable @Positive(message = "L'identifiant doit être un entier positif") Long id) {
        log.info("GET /api/destinataires/{}", id);
        DestinataireDTO dto = destinataireService.findById(id)
                .orElseThrow(() -> new NotFoundException("Destinataire introuvable id=" + id));
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<DestinataireDTO> createDestinataire(@Valid @RequestBody DestinataireDTO destinataireDTO) {
        log.info("POST /api/destinataires");
        DestinataireDTO savedDestinataire = destinataireService.save(destinataireDTO);
        return new ResponseEntity<>(savedDestinataire, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DestinataireDTO> updateDestinataire(
            @PathVariable @Positive(message = "L'identifiant doit être un entier positif") Long id,
            @Valid @RequestBody DestinataireDTO destinataireDTO) {
        destinataireService.findById(id)
                .orElseThrow(() -> new NotFoundException("Destinataire introuvable id=" + id));
        destinataireDTO.setId(id);
        log.info("PUT /api/destinataires/{}", id);
        DestinataireDTO updatedDestinataire = destinataireService.save(destinataireDTO);
        return ResponseEntity.ok(updatedDestinataire);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDestinataire(
            @PathVariable @Positive(message = "L'identifiant doit être un entier positif") Long id) {
        destinataireService.findById(id)
                .orElseThrow(() -> new NotFoundException("Destinataire introuvable id=" + id));
        log.info("DELETE /api/destinataires/{}", id);
        destinataireService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
