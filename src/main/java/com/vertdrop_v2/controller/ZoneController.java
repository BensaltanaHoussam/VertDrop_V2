package com.vertdrop_v2.controller;

import com.vertdrop_v2.dto.ZoneDTO;
import com.vertdrop_v2.service.ZoneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
public class ZoneController {

    private final ZoneService zoneService;

    public ZoneController(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @GetMapping
    public ResponseEntity<List<ZoneDTO>> getAllZones() {
        return ResponseEntity.ok(zoneService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ZoneDTO> getZoneById(@PathVariable Long id) {
        return zoneService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ZoneDTO> createZone(@RequestBody ZoneDTO zoneDTO) {
        ZoneDTO savedZone = zoneService.save(zoneDTO);
        return new ResponseEntity<>(savedZone, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ZoneDTO> updateZone(@PathVariable Long id, @RequestBody ZoneDTO zoneDTO) {
        if (!zoneService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        zoneDTO.setId(id);
        ZoneDTO updatedZone = zoneService.save(zoneDTO);
        return ResponseEntity.ok(updatedZone);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(@PathVariable Long id) {
        if (!zoneService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        zoneService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}