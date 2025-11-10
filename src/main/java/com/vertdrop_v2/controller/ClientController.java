package com.vertdrop_v2.controller;

import com.vertdrop_v2.dto.ClientExpediteurDTO;
import com.vertdrop_v2.exception.NotFoundException;
import com.vertdrop_v2.service.ClientService;
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
@RequestMapping("/api/clients")
@Validated
public class ClientController {

    private static final Logger log = LoggerFactory.getLogger(ClientController.class);
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<List<ClientExpediteurDTO>> getAllClients() {
        log.info("GET /api/clients");
        List<ClientExpediteurDTO> clients = clientService.findAll();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientExpediteurDTO> getClientById(
            @PathVariable @Positive(message = "L'identifiant doit être un entier positif") Long id) {
        log.info("GET /api/clients/{}", id);
        ClientExpediteurDTO dto = clientService.findById(id)
                .orElseThrow(() -> new NotFoundException("Client introuvable id=" + id));
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ClientExpediteurDTO> createClient(@Valid @RequestBody ClientExpediteurDTO clientDTO) {
        log.info("POST /api/clients email={}", clientDTO.getEmail());
        ClientExpediteurDTO savedClient = clientService.save(clientDTO);
        return new ResponseEntity<>(savedClient, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientExpediteurDTO> updateClient(
            @PathVariable @Positive(message = "L'identifiant doit être un entier positif") Long id,
            @Valid @RequestBody ClientExpediteurDTO clientDTO) {
        clientService.findById(id)
                .orElseThrow(() -> new NotFoundException("Client introuvable id=" + id));
        clientDTO.setId(id);
        log.info("PUT /api/clients/{}", id);
        ClientExpediteurDTO updatedClient = clientService.save(clientDTO);
        return ResponseEntity.ok(updatedClient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(
            @PathVariable @Positive(message = "L'identifiant doit être un entier positif") Long id) {
        clientService.findById(id)
                .orElseThrow(() -> new NotFoundException("Client introuvable id=" + id));
        log.info("DELETE /api/clients/{}", id);
        clientService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
