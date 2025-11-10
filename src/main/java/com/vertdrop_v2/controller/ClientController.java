package com.vertdrop_v2.controller;

import com.vertdrop_v2.dto.ClientExpediteurDTO;
import com.vertdrop_v2.exception.NotFoundException;
import com.vertdrop_v2.service.ClientService;
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
@RequestMapping("/api/clients")
@Tag(name = "Clients", description = "Endpoints de gestion des clients expéditeurs")
@Validated
public class ClientController {

    private static final Logger log = LoggerFactory.getLogger(ClientController.class);
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    @Operation(summary = "Lister les clients", description = "Retourne la liste de tous les clients expéditeurs.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
    })
    public ResponseEntity<List<ClientExpediteurDTO>> getAllClients() {
        log.info("GET /api/clients");
        return ResponseEntity.ok(clientService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un client", description = "Retourne un client expéditeur par son identifiant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client trouvé"),
            @ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    public ResponseEntity<ClientExpediteurDTO> getClientById(
            @PathVariable @Positive Long id) {
        log.info("GET /api/clients/{}", id);
        ClientExpediteurDTO dto = clientService.findById(id)
                .orElseThrow(() -> new NotFoundException("Client introuvable id=" + id));
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @Operation(summary = "Créer un client", description = "Crée un nouveau client expéditeur.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Client créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<ClientExpediteurDTO> createClient(@Valid @RequestBody ClientExpediteurDTO clientDTO) {
        log.info("POST /api/clients email={}", clientDTO.getEmail());
        return new ResponseEntity<>(clientService.save(clientDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un client", description = "Modifie les informations d\'un client existant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client mis à jour"),
            @ApiResponse(responseCode = "404", description = "Client introuvable"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<ClientExpediteurDTO> updateClient(
            @PathVariable @Positive Long id,
            @Valid @RequestBody ClientExpediteurDTO clientDTO) {
        clientService.findById(id)
                .orElseThrow(() -> new NotFoundException("Client introuvable id=" + id));
        clientDTO.setId(id);
        log.info("PUT /api/clients/{}", id);
        return ResponseEntity.ok(clientService.save(clientDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un client", description = "Supprime un client expéditeur.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Suppression effectuée"),
            @ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    public ResponseEntity<Void> deleteClient(@PathVariable @Positive Long id) {
        clientService.findById(id)
                .orElseThrow(() -> new NotFoundException("Client introuvable id=" + id));
        log.info("DELETE /api/clients/{}", id);
        clientService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
