package com.vertdrop_v2.controller;

import com.vertdrop_v2.dto.ColisDTO;
import com.vertdrop_v2.dto.LivreurDTO;
import com.vertdrop_v2.service.ColisService;
import com.vertdrop_v2.service.LivreurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livreurs")
public class LivreurController {

    private final LivreurService livreurService;
    private final ColisService colisService;

    public LivreurController(LivreurService livreurService , ColisService colisService) {
        this.livreurService = livreurService;
        this.colisService = colisService;
    }

    @GetMapping
    public ResponseEntity<List<LivreurDTO>> getAllLivreurs() {
        return ResponseEntity.ok(livreurService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivreurDTO> getLivreurById(@PathVariable Long id) {
        return livreurService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<LivreurDTO> createLivreur(@RequestBody LivreurDTO livreurDTO) {
        LivreurDTO savedLivreur = livreurService.save(livreurDTO);
        return new ResponseEntity<>(savedLivreur, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LivreurDTO> updateLivreur(@PathVariable Long id, @RequestBody LivreurDTO livreurDTO) {
        if (!livreurService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        livreurDTO.setId(id);
        LivreurDTO updatedLivreur = livreurService.save(livreurDTO);
        return ResponseEntity.ok(updatedLivreur);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLivreur(@PathVariable Long id) {
        if (!livreurService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        livreurService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}/colis")
    public ResponseEntity<List<ColisDTO>> getColisForLivreur(@PathVariable Long id) {
        if (!livreurService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        List<ColisDTO> colisList = colisService.findByLivreurId(id);
        return ResponseEntity.ok(colisList);
    }
}