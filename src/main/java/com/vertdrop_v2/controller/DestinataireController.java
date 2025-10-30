package com.vertdrop_v2.controller;

import com.vertdrop_v2.dto.DestinataireDTO;
import com.vertdrop_v2.service.DestinataireService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/destinataires")
public class DestinataireController {

    private final DestinataireService destinataireService;

    public DestinataireController(DestinataireService destinataireService) {
        this.destinataireService = destinataireService;
    }

    @GetMapping
    public ResponseEntity<List<DestinataireDTO>> getAllDestinataires() {
        return ResponseEntity.ok(destinataireService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DestinataireDTO> getDestinataireById(@PathVariable Long id) {
        return destinataireService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DestinataireDTO> createDestinataire(@RequestBody DestinataireDTO destinataireDTO) {
        DestinataireDTO savedDestinataire = destinataireService.save(destinataireDTO);
        return new ResponseEntity<>(savedDestinataire, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DestinataireDTO> updateDestinataire(@PathVariable Long id, @RequestBody DestinataireDTO destinataireDTO) {
        if (!destinataireService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        destinataireDTO.setId(id);
        DestinataireDTO updatedDestinataire = destinataireService.save(destinataireDTO);
        return ResponseEntity.ok(updatedDestinataire);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDestinataire(@PathVariable Long id) {
        if (!destinataireService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        destinataireService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}