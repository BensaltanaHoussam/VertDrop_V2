package com.vertdrop_v2.controller;

import com.vertdrop_v2.dto.ClientExpediteurDTO;
import com.vertdrop_v2.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    // 3. Inject the service layer (NOT the repository).
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    // [GET] /api/clients - Get all clients
    @GetMapping
    public ResponseEntity<List<ClientExpediteurDTO>> getAllClients() {
        List<ClientExpediteurDTO> clients = clientService.findAll();
        return ResponseEntity.ok(clients); // Returns HTTP 200 OK with the list of clients.
    }

    // [GET] /api/clients/{id} - Get a single client by ID
    @GetMapping("/{id}")
    public ResponseEntity<ClientExpediteurDTO> getClientById(@PathVariable Long id) {
        return clientService.findById(id)
                .map(ResponseEntity::ok) // If found, return HTTP 200 OK with the client.
                .orElse(ResponseEntity.notFound().build()); // If not found, return HTTP 404 Not Found.
    }

    // [POST] /api/clients - Create a new client
    @PostMapping
    public ResponseEntity<ClientExpediteurDTO> createClient(@RequestBody ClientExpediteurDTO clientDTO) {
        ClientExpediteurDTO savedClient = clientService.save(clientDTO);
        return new ResponseEntity<>(savedClient, HttpStatus.CREATED); // Returns HTTP 201 Created with the new client.
    }

    // [PUT] /api/clients/{id} - Update an existing client
    @PutMapping("/{id}")
    public ResponseEntity<ClientExpediteurDTO> updateClient(@PathVariable Long id, @RequestBody ClientExpediteurDTO clientDTO) {
        if (!clientService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        clientDTO.setId(id);
        ClientExpediteurDTO updatedClient = clientService.save(clientDTO);
        return ResponseEntity.ok(updatedClient);
    }

    // [DELETE] /api/clients/{id} - Delete a client
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        if (!clientService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        clientService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}