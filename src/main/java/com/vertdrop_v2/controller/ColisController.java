package com.vertdrop_v2.controller;

import com.vertdrop_v2.dto.ColisDTO;
import com.vertdrop_v2.dto.UpdateStatusRequestDTO;
import com.vertdrop_v2.service.ColisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colis")
public class ColisController {

    private final ColisService colisService;

    public ColisController(ColisService colisService) {
        this.colisService = colisService;
    }

    @GetMapping
    public ResponseEntity<List<ColisDTO>> getAllColis() {
        return ResponseEntity.ok(colisService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColisDTO> getColisById(@PathVariable Long id) {
        return colisService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ColisDTO> createColis(@RequestBody ColisDTO colisDTO) {
        ColisDTO savedColis = colisService.save(colisDTO);
        return new ResponseEntity<>(savedColis, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ColisDTO> updateColis(@PathVariable Long id, @RequestBody ColisDTO colisDTO) {
        if (!colisService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        colisDTO.setId(id);
        ColisDTO updatedColis = colisService.save(colisDTO);
        return ResponseEntity.ok(updatedColis);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteColis(@PathVariable Long id) {
        if (!colisService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        colisService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ColisDTO> updateColisStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequestDTO statusRequest) {

        try {
            ColisDTO updatedColis = colisService.updateStatus(
                    id,
                    statusRequest.getNewStatus(),
                    statusRequest.getComment()
            );
            return ResponseEntity.ok(updatedColis);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}