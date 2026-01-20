package com.vertdrop_v2.service.impl;

import com.vertdrop_v2.dto.ClientExpediteurDTO;
import com.vertdrop_v2.dto.ColisCreateRequestDTO;
import com.vertdrop_v2.dto.ColisDTO;
import com.vertdrop_v2.dto.ColisProduitDTO;
import com.vertdrop_v2.dto.DestinataireDTO;
import com.vertdrop_v2.dto.HistoriqueLivraisonDTO;
import com.vertdrop_v2.dto.LivreurDTO;
import com.vertdrop_v2.dto.ProduitDTO;
import com.vertdrop_v2.dto.ZoneDTO;
import com.vertdrop_v2.entity.Colis;
import com.vertdrop_v2.entity.ColisProduit;
import com.vertdrop_v2.entity.ColisProduitId;
import com.vertdrop_v2.entity.HistoriqueLivraison;
import com.vertdrop_v2.entity.Produit;
import com.vertdrop_v2.entity.StatutColis;
import com.vertdrop_v2.mapper.ColisMapper;
import com.vertdrop_v2.mapper.HistoriqueLivraisonMapper;
import com.vertdrop_v2.repository.ColisRepository;
import com.vertdrop_v2.repository.HistoriqueLivraisonRepository;
import com.vertdrop_v2.repository.LivreurRepository;
import com.vertdrop_v2.repository.ProduitRepository;
import com.vertdrop_v2.service.ColisService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ColisServiceImpl implements ColisService {

    private final ColisRepository colisRepository;
    private final HistoriqueLivraisonRepository historiqueLivraisonRepository;
    private final HistoriqueLivraisonMapper historiqueLivraisonMapper;
    private final LivreurRepository livreurRepository;
    private final ProduitRepository produitRepository;
    private final ColisMapper colisMapper;

    public ColisServiceImpl(ColisRepository colisRepository,
            HistoriqueLivraisonRepository historiqueLivraisonRepository,
            HistoriqueLivraisonMapper historiqueLivraisonMapper,
            LivreurRepository livreurRepository,
            ProduitRepository produitRepository,
            ColisMapper colisMapper) {
        this.colisRepository = colisRepository;
        this.historiqueLivraisonRepository = historiqueLivraisonRepository;
        this.historiqueLivraisonMapper = historiqueLivraisonMapper;
        this.livreurRepository = livreurRepository;
        this.produitRepository = produitRepository;
        this.colisMapper = colisMapper;
    }

    @Override
    public ColisDTO createFromRequest(ColisCreateRequestDTO req) {
        ColisDTO dto = new ColisDTO();
        dto.setDescription(req.getDescription());
        dto.setPoids(req.getPoids());
        dto.setStatut(StatutColis.valueOf(req.getStatut().toUpperCase()));
        dto.setPriorite(req.getPriorite());
        dto.setVilleDestination(req.getVilleDestination());

        ClientExpediteurDTO client = new ClientExpediteurDTO();
        client.setId(req.getClientExpediteurId());
        dto.setClientExpediteur(client);

        DestinataireDTO dest = new DestinataireDTO();
        dest.setId(req.getDestinataireId());
        dto.setDestinataire(dest);

        if (req.getLivreurId() != null) {
            LivreurDTO liv = new LivreurDTO();
            liv.setId(req.getLivreurId());
            dto.setLivreur(liv);
        }

        ZoneDTO zone = new ZoneDTO();
        zone.setId(req.getZoneId());
        dto.setZone(zone);

        if (req.getProduits() != null && !req.getProduits().isEmpty()) {
            List<ColisProduitDTO> items = new ArrayList<>();
            for (ColisCreateRequestDTO.ColisProduitItemRequest p : req.getProduits()) {
                ColisProduitDTO line = new ColisProduitDTO();
                ProduitDTO prod = new ProduitDTO();
                prod.setId(p.getProduitId());
                line.setProduit(prod);
                line.setQuantite(p.getQuantite());
                line.setPrix(p.getPrix()); // si null, prendra le prix du produit
                items.add(line);
            }
            dto.setColisProduits(items);
        }

        return save(dto);
    }

    @Override
    public ColisDTO save(ColisDTO colisDTO) {
        if (colisDTO == null)
            throw new IllegalArgumentException("ColisDTO ne peut pas être nul.");

        // Mise à jour
        if (colisDTO.getId() != null) {
            Colis cible = colisRepository.findById(colisDTO.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Colis introuvable id=" + colisDTO.getId()));

            Colis temp = colisMapper.toEntity(colisDTO);
            cible.setDescription(temp.getDescription());
            cible.setPoids(temp.getPoids());
            cible.setStatut(temp.getStatut());
            cible.setPriorite(temp.getPriorite());
            cible.setVilleDestination(temp.getVilleDestination());
            cible.setClientExpediteur(temp.getClientExpediteur());
            cible.setDestinataire(temp.getDestinataire());
            cible.setLivreur(temp.getLivreur());
            cible.setZone(temp.getZone());

            cible.getColisProduits().clear();
            rebuildColisProduits(cible, colisDTO.getColisProduits());

            Colis saved = colisRepository.save(cible);
            return colisMapper.toDto(saved);
        }

        // Création
        Colis entity = colisMapper.toEntity(colisDTO);
        entity.setColisProduits(new ArrayList<>());
        Colis saved = colisRepository.save(entity);

        rebuildColisProduits(saved, colisDTO.getColisProduits());
        Colis savedWithProducts = colisRepository.save(saved);
        return colisMapper.toDto(savedWithProducts);
    }

    private void rebuildColisProduits(Colis colis, List<ColisProduitDTO> items) {
        if (items == null || items.isEmpty())
            return;

        for (ColisProduitDTO it : items) {
            if (it.getProduit() == null || it.getProduit().getId() == null) {
                throw new IllegalArgumentException("Chaque produit du colis doit contenir un 'produit.id'.");
            }
            if (it.getQuantite() == null || it.getQuantite() < 1) {
                throw new IllegalArgumentException("La quantité doit être >= 1.");
            }

            Produit produit = produitRepository.findById(it.getProduit().getId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Produit introuvable id=" + it.getProduit().getId()));

            ColisProduit cp = new ColisProduit();
            ColisProduitId id = new ColisProduitId();
            id.setIdColis(colis.getId());
            id.setIdProduit(produit.getId());
            cp.setId(id);

            cp.setColis(colis);
            cp.setProduit(produit);
            cp.setQuantite(it.getQuantite());
            cp.setPrix(it.getPrix() != null ? it.getPrix() : produit.getPrix());
            cp.setDateAjout(it.getDateAjout() != null ? it.getDateAjout() : LocalDateTime.now());

            colis.getColisProduits().add(cp);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ColisDTO> findById(Long id) {
        return colisRepository.findById(id).map(colisMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ColisDTO> findAll(Pageable pageable, StatutColis statut, Long zoneId, String keyword, Long clientId,
            Long livreurId) {

        if (keyword == null) {
            keyword = "";
        }

        return colisRepository.findWithFilters(statut, zoneId, keyword, clientId, livreurId, pageable)
                .map(colisMapper::toDto);
    }

    @Override
    public void deleteById(Long id) {
        colisRepository.deleteById(id);
    }

    @Override
    public ColisDTO updateStatus(Long colisId, StatutColis newStatus, String comment) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new EntityNotFoundException("Colis introuvable id=" + colisId));

        colis.setStatut(newStatus);

        HistoriqueLivraison historyRecord = new HistoriqueLivraison();
        historyRecord.setColis(colis);
        historyRecord.setStatut(newStatus);
        historyRecord.setDateChangement(LocalDateTime.now());
        historyRecord.setCommentaire(comment);
        historiqueLivraisonRepository.save(historyRecord);

        return colisMapper.toDto(colis);
    }

    @Override
    public ColisDTO assignLivreur(Long colisId, Long livreurId) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new EntityNotFoundException("Colis introuvable id=" + colisId));
        livreurRepository.findById(livreurId)
                .orElseThrow(() -> new EntityNotFoundException("Livreur introuvable id=" + livreurId));

        colis.setLivreur(livreurRepository.getReferenceById(livreurId));
        return colisMapper.toDto(colis);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalWeightByZone(Long zoneId) {
        return colisRepository.sumPoidsByZone(zoneId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ColisDTO> findByLivreurId(Long livreurId) {
        return colisRepository.findByLivreurId(livreurId).stream()
                .map(colisMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoriqueLivraisonDTO> findHistoryForColis(Long colisId) {
        return historiqueLivraisonRepository.findByColisIdOrderByDateChangementDesc(colisId).stream()
                .map(historiqueLivraisonMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ColisDTO> findAllForLivreur(
            Long livreurId,
            Pageable pageable,
            StatutColis statut,
            Long zoneId,
            String keyword) {

        if (keyword == null) {
            keyword = "";
        }

        return colisRepository
                .findWithFiltersAndLivreur(statut, zoneId, keyword, livreurId, pageable)
                .map(colisMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ColisDTO> findAllForClient(
            Long clientId,
            Pageable pageable,
            StatutColis statut,
            Long zoneId,
            String keyword) {

        if (keyword == null) {
            keyword = "";
        }

        return colisRepository
                .findWithFiltersAndClient(statut, zoneId, keyword, clientId, pageable)
                .map(colisMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ColisDTO> findByClientId(Long clientId) {
        return colisRepository.findByClientExpediteurId(clientId).stream()
                .map(colisMapper::toDto)
                .collect(Collectors.toList());
    }

}
