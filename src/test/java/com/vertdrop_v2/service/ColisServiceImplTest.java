package com.vertdrop_v2.service;

import com.vertdrop_v2.dto.*;
import com.vertdrop_v2.entity.*;
import com.vertdrop_v2.mapper.ColisMapper;
import com.vertdrop_v2.mapper.HistoriqueLivraisonMapper;
import com.vertdrop_v2.repository.*;
import com.vertdrop_v2.service.impl.ColisServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ColisServiceImplTest {

    @Mock
    private ColisRepository colisRepository;
    @Mock
    private HistoriqueLivraisonRepository historiqueLivraisonRepository;
    @Mock
    private HistoriqueLivraisonMapper historiqueLivraisonMapper;
    @Mock
    private LivreurRepository livreurRepository;
    @Mock
    private ProduitRepository produitRepository;
    @Mock
    private ColisMapper colisMapper;

    @InjectMocks
    private ColisServiceImpl service;

    private Colis colisEntity;
    private ColisDTO colisDTO;

    private Produit produitEntity;

    @BeforeEach
    void setUp() {
        colisEntity = new Colis();
        colisEntity.setId(1L);
        colisEntity.setDescription("Test colis");
        colisEntity.setStatut(StatutColis.CREE);
        colisEntity.setColisProduits(new ArrayList<>());

        colisDTO = new ColisDTO();
        colisDTO.setId(1L);
        colisDTO.setDescription("Test colis");
        colisDTO.setStatut(StatutColis.CREE);

        produitEntity = new Produit();
        produitEntity.setId(10L);
        produitEntity.setPrix(BigDecimal.valueOf(25));

        // Mapper simulé (lenient pour éviter UnnecessaryStubbing quand non utilisé par certains tests)
        lenient().when(colisMapper.toDto(any(Colis.class))).thenAnswer(inv -> {
            Colis c = inv.getArgument(0);
            ColisDTO d = new ColisDTO();
            d.setId(c.getId());
            d.setDescription(c.getDescription());
            d.setStatut(c.getStatut());
            d.setColisProduits(new ArrayList<>());
            return d;
        });

        lenient().when(colisMapper.toEntity(any(ColisDTO.class))).thenAnswer(inv -> {
            ColisDTO d = inv.getArgument(0);
            Colis c = new Colis();
            c.setId(d.getId());
            c.setDescription(d.getDescription());
            c.setStatut(d.getStatut());
            c.setPoids(d.getPoids());
            c.setPriorite(d.getPriorite());
            c.setVilleDestination(d.getVilleDestination());
            c.setClientExpediteur(Optional.ofNullable(d.getClientExpediteur()).map(x -> {
                ClientExpediteur ce = new ClientExpediteur();
                ce.setId(x.getId());
                return ce;
            }).orElse(null));
            c.setDestinataire(Optional.ofNullable(d.getDestinataire()).map(x -> {
                Destinataire de = new Destinataire();
                de.setId(x.getId());
                return de;
            }).orElse(null));
            c.setLivreur(Optional.ofNullable(d.getLivreur()).map(x -> {
                Livreur l = new Livreur();
                l.setId(x.getId());
                return l;
            }).orElse(null));
            c.setZone(Optional.ofNullable(d.getZone()).map(x -> {
                Zone z = new Zone();
                z.setId(x.getId());
                return z;
            }).orElse(null));
            c.setColisProduits(new ArrayList<>());
            return c;
        });
    }

    @Test
    void createFromRequest_shouldBuildAndPersist() {
        ColisCreateRequestDTO req = new ColisCreateRequestDTO();
        req.setDescription("Nouveau colis");
        req.setPoids(BigDecimal.valueOf(2.5));
        req.setStatut("CREE");
        req.setPriorite(2);
        req.setVilleDestination("Paris");
        req.setClientExpediteurId(5L);
        req.setDestinataireId(6L);
        req.setZoneId(7L);
        ColisCreateRequestDTO.ColisProduitItemRequest item = new ColisCreateRequestDTO.ColisProduitItemRequest();
        item.setProduitId(10L);
        item.setQuantite(3);
        req.setProduits(List.of(item));

        when(colisRepository.save(any(Colis.class))).thenAnswer(new Answer<Colis>() {
            private boolean first = true;
            @Override
            public Colis answer(InvocationOnMock inv) {
                Colis c = inv.getArgument(0);
                if (first && c.getId() == null) {
                    c.setId(100L);
                    first = false;
                }
                return c;
            }
        });
        when(produitRepository.findById(10L)).thenReturn(Optional.of(produitEntity));

        ColisDTO result = service.createFromRequest(req);

        assertThat(result).isNotNull();
        verify(colisRepository, times(2)).save(any(Colis.class));
        verify(produitRepository, times(1)).findById(10L);
    }

    @Test
    void createFromRequest_withLivreur_shouldPropagateLivreur() {
        ColisCreateRequestDTO req = new ColisCreateRequestDTO();
        req.setDescription("Avec Livreur");
        req.setStatut("CREE");
        req.setClientExpediteurId(1L);
        req.setDestinataireId(2L);
        req.setZoneId(3L);
        req.setLivreurId(8L);

        ArgumentCaptor<Colis> cap = ArgumentCaptor.forClass(Colis.class);
        when(colisRepository.save(any(Colis.class))).thenAnswer(inv -> {
            Colis c = inv.getArgument(0);
            if (c.getId() == null) c.setId(999L);
            return c;
        });

        service.createFromRequest(req);

        verify(colisRepository, atLeastOnce()).save(cap.capture());
        Colis firstSaved = cap.getAllValues().get(0);
        assertThat(firstSaved.getLivreur()).isNotNull();
        assertThat(firstSaved.getLivreur().getId()).isEqualTo(8L);
    }

    @Test
    void save_createWithoutProducts_shouldPersist() {
        ColisDTO dto = new ColisDTO();
        dto.setDescription("Sans produits");
        when(colisRepository.save(any(Colis.class))).thenAnswer(inv -> {
            Colis c = inv.getArgument(0);
            if (c.getId() == null) c.setId(200L);
            return c;
        });

        ColisDTO saved = service.save(dto);

        assertThat(saved.getId()).isEqualTo(200L);
        verify(colisRepository, times(2)).save(any(Colis.class));
    }

    @Test
    void save_updateExisting_shouldModifyFields() {
        colisDTO.setDescription("Modifié");
        when(colisRepository.findById(1L)).thenReturn(Optional.of(colisEntity));
        when(colisRepository.save(any(Colis.class))).thenAnswer(inv -> inv.getArgument(0));

        ColisDTO updated = service.save(colisDTO);

        assertThat(updated.getDescription()).isEqualTo("Modifié");
        verify(colisRepository).findById(1L);
        verify(colisRepository).save(colisEntity);
    }

    @Test
    void save_updateExisting_withProducts_shouldRebuildLines() {
        when(colisRepository.findById(1L)).thenReturn(Optional.of(colisEntity));
        when(colisRepository.save(any(Colis.class))).thenAnswer(inv -> inv.getArgument(0));
        when(produitRepository.findById(10L)).thenReturn(Optional.of(produitEntity));

        ColisProduitDTO cp = new ColisProduitDTO();
        ProduitDTO p = new ProduitDTO();
        p.setId(10L);
        cp.setProduit(p);
        cp.setQuantite(2);
        colisDTO.setColisProduits(List.of(cp));

        ArgumentCaptor<Colis> cap = ArgumentCaptor.forClass(Colis.class);

        service.save(colisDTO);

        verify(colisRepository, atLeastOnce()).save(cap.capture());
        Colis saved = cap.getValue();
        assertThat(saved.getColisProduits()).hasSize(1);
        ColisProduit line = saved.getColisProduits().get(0);
        assertThat(line.getProduit().getId()).isEqualTo(10L);
        assertThat(line.getQuantite()).isEqualTo(2);
        assertThat(line.getPrix()).isEqualTo(produitEntity.getPrix());
    }

    @Test
    void save_withNull_shouldThrow() {
        assertThatThrownBy(() -> service.save(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ColisDTO");
    }

    @Test
    void save_updateNotFound_shouldThrow() {
        ColisDTO dto = new ColisDTO();
        dto.setId(404L);
        when(colisRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Colis introuvable");
    }

    @Test
    void save_withProductMissingId_shouldThrow() {
        ColisDTO dto = new ColisDTO();
        ColisProduitDTO cp = new ColisProduitDTO();
        cp.setQuantite(1);
        dto.setColisProduits(List.of(cp));

        when(colisRepository.save(any(Colis.class))).thenAnswer(inv -> {
            Colis c = inv.getArgument(0);
            if (c.getId() == null) c.setId(300L);
            return c;
        });

        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("produit.id");
    }

    @Test
    void save_withInvalidQuantity_shouldThrow() {
        ColisDTO dto = new ColisDTO();
        ColisProduitDTO cp = new ColisProduitDTO();
        ProduitDTO p = new ProduitDTO();
        p.setId(10L);
        cp.setProduit(p);
        cp.setQuantite(0);
        dto.setColisProduits(List.of(cp));

        when(colisRepository.save(any(Colis.class))).thenAnswer(inv -> {
            Colis c = inv.getArgument(0);
            if (c.getId() == null) c.setId(310L);
            return c;
        });

        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("quantité");
    }

    @Test
    void save_withUnknownProduct_shouldThrow() {
        ColisDTO dto = new ColisDTO();
        ColisProduitDTO cp = new ColisProduitDTO();
        ProduitDTO p = new ProduitDTO();
        p.setId(999L);
        cp.setProduit(p);
        cp.setQuantite(1);
        dto.setColisProduits(List.of(cp));

        when(colisRepository.save(any(Colis.class))).thenAnswer(inv -> {
            Colis c = inv.getArgument(0);
            if (c.getId() == null) c.setId(320L);
            return c;
        });
        when(produitRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Produit introuvable");
    }

    @Test
    void save_withProducts_customPriceAndDate_shouldUseProvidedValues() {
        when(produitRepository.findById(10L)).thenReturn(Optional.of(produitEntity));
        when(colisRepository.save(any(Colis.class))).thenAnswer(inv -> {
            Colis c = inv.getArgument(0);
            if (c.getId() == null) c.setId(777L);
            return c;
        });

        ColisDTO dto = new ColisDTO();
        ColisProduitDTO cp = new ColisProduitDTO();
        ProduitDTO p = new ProduitDTO();
        p.setId(10L);
        cp.setProduit(p);
        cp.setQuantite(5);
        cp.setPrix(BigDecimal.valueOf(99));
        LocalDateTime fixed = LocalDateTime.of(2024, 1, 1, 12, 0);
        cp.setDateAjout(fixed);
        dto.setColisProduits(List.of(cp));

        ArgumentCaptor<Colis> cap = ArgumentCaptor.forClass(Colis.class);

        service.save(dto);

        verify(colisRepository, atLeastOnce()).save(cap.capture());
        Colis saved = cap.getAllValues().get(cap.getAllValues().size() - 1);
        assertThat(saved.getColisProduits()).hasSize(1);
        ColisProduit line = saved.getColisProduits().get(0);
        assertThat(line.getPrix()).isEqualTo(BigDecimal.valueOf(99));
        assertThat(line.getDateAjout()).isEqualTo(fixed);
    }

    @Test
    void updateStatus_shouldPersistHistory() {
        when(colisRepository.findById(1L)).thenReturn(Optional.of(colisEntity));

        ColisDTO res = service.updateStatus(1L, StatutColis.EN_TRANSIT, "Départ");

        assertThat(colisEntity.getStatut()).isEqualTo(StatutColis.EN_TRANSIT);
        assertThat(res.getStatut()).isEqualTo(StatutColis.EN_TRANSIT);
        verify(historiqueLivraisonRepository).save(any(HistoriqueLivraison.class));
    }

    @Test
    void updateStatus_colisNotFound_shouldThrow() {
        when(colisRepository.findById(55L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.updateStatus(55L, StatutColis.LIVRE, ""))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Colis introuvable");
    }

    @Test
    void assignLivreur_ok() {
        Livreur livreur = new Livreur();
        livreur.setId(9L);

        when(colisRepository.findById(1L)).thenReturn(Optional.of(colisEntity));
        when(livreurRepository.findById(9L)).thenReturn(Optional.of(livreur));
        when(livreurRepository.getReferenceById(9L)).thenReturn(livreur);

        ColisDTO res = service.assignLivreur(1L, 9L);

        assertThat(colisEntity.getLivreur()).isNotNull();
        assertThat(res.getId()).isEqualTo(1L);
    }

    @Test
    void assignLivreur_colisNotFound_shouldThrow() {
        when(colisRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.assignLivreur(1L, 2L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Colis introuvable");
    }

    @Test
    void assignLivreur_livreurNotFound_shouldThrow() {
        when(colisRepository.findById(1L)).thenReturn(Optional.of(colisEntity));
        when(livreurRepository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.assignLivreur(1L, 2L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Livreur introuvable");
    }

    @Test
    void findAll_withFilters() {
        Page<Colis> page = new PageImpl<>(List.of(colisEntity));
        when(colisRepository.findWithFilters(any(), any(), any(), any(Pageable.class))).thenReturn(page);

        Page<ColisDTO> result = service.findAll(PageRequest.of(0, 10, Sort.by("poids")), StatutColis.CREE, 3L, "test");

        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(colisRepository).findWithFilters(eq(StatutColis.CREE), eq(3L), eq("test"), any(Pageable.class));
    }

    @Test
    void findById_ok() {
        when(colisRepository.findById(1L)).thenReturn(Optional.of(colisEntity));

        Optional<ColisDTO> opt = service.findById(1L);

        assertThat(opt).isPresent();
    }

    @Test
    void findById_empty() {
        when(colisRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<ColisDTO> opt = service.findById(99L);

        assertThat(opt).isNotPresent();
    }

    @Test
    void calculateTotalWeightByZone_ok() {
        when(colisRepository.sumPoidsByZone(5L)).thenReturn(BigDecimal.valueOf(123.45));

        BigDecimal total = service.calculateTotalWeightByZone(5L);

        assertThat(total).isEqualTo(BigDecimal.valueOf(123.45));
        verify(colisRepository).sumPoidsByZone(5L);
    }

    @Test
    void findByLivreurId_ok() {
        Colis other = new Colis();
        other.setId(2L);
        other.setDescription("Second");
        when(colisRepository.findByLivreurId(9L)).thenReturn(List.of(colisEntity, other));

        List<ColisDTO> list = service.findByLivreurId(9L);

        assertThat(list).hasSize(2);
        verify(colisRepository).findByLivreurId(9L);
    }

    @Test
    void findHistoryForColis_ok() {
        HistoriqueLivraison h = new HistoriqueLivraison();
        h.setId(50L);
        h.setDateChangement(LocalDateTime.now());
        h.setStatut(StatutColis.CREE);
        when(historiqueLivraisonRepository.findByColisIdOrderByDateChangementDesc(1L))
                .thenReturn(List.of(h));
        when(historiqueLivraisonMapper.toDto(any(HistoriqueLivraison.class))).thenAnswer(inv -> {
            HistoriqueLivraison src = inv.getArgument(0);
            HistoriqueLivraisonDTO dto = new HistoriqueLivraisonDTO();
            dto.setId(src.getId());
            dto.setStatut(src.getStatut());
            return dto;
        });

        List<HistoriqueLivraisonDTO> history = service.findHistoryForColis(1L);

        assertThat(history).hasSize(1);
        assertThat(history.get(0).getId()).isEqualTo(50L);
    }

    @Test
    void deleteById_shouldCallRepository() {
        service.deleteById(1L);
        verify(colisRepository).deleteById(1L);
    }
}
