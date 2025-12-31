package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.HistoriqueLivraisonDTO;
import com.vertdrop_v2.entity.HistoriqueLivraison;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-31T16:51:19+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class HistoriqueLivraisonMapperImpl implements HistoriqueLivraisonMapper {

    @Override
    public HistoriqueLivraisonDTO toDto(HistoriqueLivraison historiqueLivraison) {
        if ( historiqueLivraison == null ) {
            return null;
        }

        HistoriqueLivraisonDTO historiqueLivraisonDTO = new HistoriqueLivraisonDTO();

        historiqueLivraisonDTO.setId( historiqueLivraison.getId() );
        historiqueLivraisonDTO.setStatut( historiqueLivraison.getStatut() );
        historiqueLivraisonDTO.setDateChangement( historiqueLivraison.getDateChangement() );
        historiqueLivraisonDTO.setCommentaire( historiqueLivraison.getCommentaire() );

        return historiqueLivraisonDTO;
    }
}
