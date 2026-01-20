package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.HistoriqueLivraisonDTO;
import com.vertdrop_v2.entity.HistoriqueLivraison;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-20T09:19:40+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260101-2150, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class HistoriqueLivraisonMapperImpl implements HistoriqueLivraisonMapper {

    @Override
    public HistoriqueLivraisonDTO toDto(HistoriqueLivraison historiqueLivraison) {
        if ( historiqueLivraison == null ) {
            return null;
        }

        HistoriqueLivraisonDTO historiqueLivraisonDTO = new HistoriqueLivraisonDTO();

        historiqueLivraisonDTO.setCommentaire( historiqueLivraison.getCommentaire() );
        historiqueLivraisonDTO.setDateChangement( historiqueLivraison.getDateChangement() );
        historiqueLivraisonDTO.setId( historiqueLivraison.getId() );
        historiqueLivraisonDTO.setStatut( historiqueLivraison.getStatut() );

        return historiqueLivraisonDTO;
    }
}
