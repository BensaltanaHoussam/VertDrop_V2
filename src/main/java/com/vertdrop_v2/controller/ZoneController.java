package com.vertdrop_v2.controller;

import com.vertdrop_v2.dto.ZoneDTO;
import com.vertdrop_v2.exception.NotFoundException;
import com.vertdrop_v2.service.ZoneService;
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
@RequestMapping("/api/zones")
@Validated
public class ZoneController {

    private static final Logger log = LoggerFactory.getLogger(ZoneController.class);
    private final ZoneService zoneService;

    public ZoneController(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @GetMapping
    public ResponseEntity<List<ZoneDTO>> getAllZones() {
        log.info("GET /api/zones");
        return ResponseEntity.ok(zoneService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ZoneDTO> getZoneById(
            @PathVariable @Positive(message = "L'identifiant doit être un entier positif") Long id) {
        log.info("GET /api/zones/{}", id);
        ZoneDTO dto = zoneService.findById(id)
                .orElseThrow(() -> new NotFoundException("Zone introuvable id=" + id));
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ZoneDTO> createZone(@Valid @RequestBody ZoneDTO zoneDTO) {
        log.info("POST /api/zones");
        ZoneDTO savedZone = zoneService.save(zoneDTO);
        return new ResponseEntity<>(savedZone, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ZoneDTO> updateZone(
            @PathVariable @Positive(message = "L'identifiant doit être un entier positif") Long id,
            @Valid @RequestBody ZoneDTO zoneDTO) {
        zoneService.findById(id)
                .orElseThrow(() -> new NotFoundException("Zone introuvable id=" + id));
        zoneDTO.setId(id);
        log.info("PUT /api/zones/{}", id);
        ZoneDTO updatedZone = zoneService.save(zoneDTO);
        return ResponseEntity.ok(updatedZone);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(
            @PathVariable @Positive(message = "L'identifiant doit être un entier positif") Long id) {
        zoneService.findById(id)
                .orElseThrow(() -> new NotFoundException("Zone introuvable id=" + id));
        log.info("DELETE /api/zones/{}", id);
        zoneService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
