package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.ColisDTO;
import com.vertdrop_v2.dto.ColisProduitDTO;
import com.vertdrop_v2.dto.HistoriqueLivraisonDTO;
import com.vertdrop_v2.entity.Colis;
import com.vertdrop_v2.entity.ColisProduit;
import com.vertdrop_v2.entity.HistoriqueLivraison;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-05T11:56:32+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260128-0750, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class ColisMapperImpl implements ColisMapper {

    @Autowired
    private ClientExpediteurMapper clientExpediteurMapper;
    @Autowired
    private DestinataireMapper destinataireMapper;
    @Autowired
    private LivreurMapper livreurMapper;
    @Autowired
    private ZoneMapper zoneMapper;
    @Autowired
    private HistoriqueLivraisonMapper historiqueLivraisonMapper;
    @Autowired
    private ColisProduitMapper colisProduitMapper;

    @Override
    public ColisDTO toDto(Colis entity) {
        if ( entity == null ) {
            return null;
        }

        ColisDTO colisDTO = new ColisDTO();

        colisDTO.setClientExpediteur( clientExpediteurMapper.toDto( entity.getClientExpediteur() ) );
        colisDTO.setColisProduits( colisProduitListToColisProduitDTOList( entity.getColisProduits() ) );
        colisDTO.setDescription( entity.getDescription() );
        colisDTO.setDestinataire( destinataireMapper.toDto( entity.getDestinataire() ) );
        colisDTO.setHistoriqueLivraisons( historiqueLivraisonListToHistoriqueLivraisonDTOList( entity.getHistoriqueLivraisons() ) );
        colisDTO.setId( entity.getId() );
        colisDTO.setLivreur( livreurMapper.toDto( entity.getLivreur() ) );
        colisDTO.setPoids( entity.getPoids() );
        colisDTO.setPriorite( entity.getPriorite() );
        colisDTO.setStatut( entity.getStatut() );
        colisDTO.setVilleDestination( entity.getVilleDestination() );
        colisDTO.setZone( zoneMapper.toDto( entity.getZone() ) );

        return colisDTO;
    }

    @Override
    public Colis toEntity(ColisDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Colis colis = new Colis();

        colis.setClientExpediteur( clientExpediteurMapper.toEntity( dto.getClientExpediteur() ) );
        colis.setDescription( dto.getDescription() );
        colis.setDestinataire( destinataireMapper.toEntity( dto.getDestinataire() ) );
        colis.setId( dto.getId() );
        colis.setLivreur( livreurMapper.toEntity( dto.getLivreur() ) );
        colis.setPoids( dto.getPoids() );
        colis.setPriorite( dto.getPriorite() );
        colis.setStatut( dto.getStatut() );
        colis.setVilleDestination( dto.getVilleDestination() );
        colis.setZone( zoneMapper.toEntity( dto.getZone() ) );

        return colis;
    }

    @Override
    public Object toDTO(Colis colis) {
        if ( colis == null ) {
            return null;
        }

        Object object = new Object();

        return object;
    }

    protected List<ColisProduitDTO> colisProduitListToColisProduitDTOList(List<ColisProduit> list) {
        if ( list == null ) {
            return null;
        }

        List<ColisProduitDTO> list1 = new ArrayList<ColisProduitDTO>( list.size() );
        for ( ColisProduit colisProduit : list ) {
            list1.add( colisProduitMapper.toDto( colisProduit ) );
        }

        return list1;
    }

    protected List<HistoriqueLivraisonDTO> historiqueLivraisonListToHistoriqueLivraisonDTOList(List<HistoriqueLivraison> list) {
        if ( list == null ) {
            return null;
        }

        List<HistoriqueLivraisonDTO> list1 = new ArrayList<HistoriqueLivraisonDTO>( list.size() );
        for ( HistoriqueLivraison historiqueLivraison : list ) {
            list1.add( historiqueLivraisonMapper.toDto( historiqueLivraison ) );
        }

        return list1;
    }
}
