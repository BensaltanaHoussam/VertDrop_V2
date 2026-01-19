package com.vertdrop_v2.controller;

import com.vertdrop_v2.dto.ClientExpediteurDTO;
import com.vertdrop_v2.exception.NotFoundException;
import com. vertdrop_v2.service.AuthService;
import com.vertdrop_v2.service.ClientService;
import io.swagger. v3.oas.annotations. Operation;
import io.swagger. v3.oas.annotations. tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger. v3.oas.annotations. responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints. Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind. annotation.*;

        import java.util. List;

@RestController
@RequestMapping("/api/clients")
@Tag(name = "Clients", description = "Endpoints de gestion des clients expéditeurs")
@Validated
public class ClientController {


    private static final Logger log = LoggerFactory.getLogger(ClientController.class);
    private final ClientService clientService;
    private final AuthService authService;

    public ClientController(ClientService clientService, AuthService authService) {
        this.clientService = clientService;
        this.authService = authService;
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Lister les clients", description = "Retourne la liste de tous les clients expéditeurs (MANAGER uniquement).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<ClientExpediteurDTO>> getAllClients() {
        log.info("GET /api/clients");
        return ResponseEntity.ok(clientService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'CLIENT')")
    @Operation(summary = "Récupérer un client", description = "Retourne un client expéditeur par son identifiant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client trouvé"),
            @ApiResponse(responseCode = "404", description = "Client introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<ClientExpediteurDTO> getClientById(
            @PathVariable @Positive Long id) {
        log.info("GET /api/clients/{}", id);

        // Client can only see himself
        if (authService.hasRole("ROLE_CLIENT")) {
            Long currentClientId = authService.getCurrentClient()
                    .orElseThrow(() -> new RuntimeException("Client not found"))
                    . getId();
            if (!id.equals(currentClientId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        ClientExpediteurDTO dto = clientService.findById(id)
                .orElseThrow(() -> new NotFoundException("Client introuvable id=" + id));
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Créer un client", description = "Crée un nouveau client expéditeur (MANAGER uniquement).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Client créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<ClientExpediteurDTO> createClient(@Valid @RequestBody ClientExpediteurDTO clientDTO) {
        log.info("POST /api/clients email={}", clientDTO.getEmail());
        return new ResponseEntity<>(clientService.save(clientDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'CLIENT')")
    @Operation(summary = "Mettre à jour un client", description = "Modifie les informations d'un client existant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client mis à jour"),
            @ApiResponse(responseCode = "404", description = "Client introuvable"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<ClientExpediteurDTO> updateClient(
            @PathVariable @Positive Long id,
            @Valid @RequestBody ClientExpediteurDTO clientDTO) {

        // Client can only update himself
        if (authService.hasRole("ROLE_CLIENT")) {
            Long currentClientId = authService.getCurrentClient()
                    .orElseThrow(() -> new RuntimeException("Client not found"))
                    . getId();
            if (!id. equals(currentClientId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        clientService.findById(id)
                .orElseThrow(() -> new NotFoundException("Client introuvable id=" + id));
        clientDTO.setId(id);
        log.info("PUT /api/clients/{}", id);
        return ResponseEntity.ok(clientService.save(clientDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Supprimer un client", description = "Supprime un client expéditeur (MANAGER uniquement).")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Suppression effectuée"),
            @ApiResponse(responseCode = "404", description = "Client introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Void> deleteClient(@PathVariable @Positive Long id) {
        clientService.findById(id)
                .orElseThrow(() -> new NotFoundException("Client introuvable id=" + id));
        log.info("DELETE /api/clients/{}", id);
        clientService.deleteById(id);
        return ResponseEntity.noContent().build();
    }



    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Mon profil", description = "Retourne le profil du client connecté.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Profil récupéré"))
    public ResponseEntity<ClientExpediteurDTO> getMyProfile() {
        log.info("GET /api/clients/me");
        ClientExpediteurDTO client = authService.getCurrentClient()
                .map(c -> clientService.findById(c.getId()).orElseThrow())
                .orElseThrow(() -> new RuntimeException("Client not found for current user"));
        return ResponseEntity.ok(client);
    }
}