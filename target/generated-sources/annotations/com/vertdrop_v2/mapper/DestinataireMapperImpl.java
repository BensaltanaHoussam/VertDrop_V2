package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.DestinataireDTO;
import com.vertdrop_v2.entity.Destinataire;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-21T09:23:36+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260101-2150, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class DestinataireMapperImpl implements DestinataireMapper {

    @Override
    public DestinataireDTO toDto(Destinataire entity) {
        if ( entity == null ) {
            return null;
        }

        DestinataireDTO destinataireDTO = new DestinataireDTO();

        destinataireDTO.setAdresse( entity.getAdresse() );
        destinataireDTO.setEmail( entity.getEmail() );
        destinataireDTO.setId( entity.getId() );
        destinataireDTO.setNom( entity.getNom() );
        destinataireDTO.setPrenom( entity.getPrenom() );
        destinataireDTO.setTelephone( entity.getTelephone() );

        return destinataireDTO;
    }

    @Override
    public Destinataire toEntity(DestinataireDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Destinataire destinataire = new Destinataire();

        destinataire.setAdresse( dto.getAdresse() );
        destinataire.setEmail( dto.getEmail() );
        destinataire.setId( dto.getId() );
        destinataire.setNom( dto.getNom() );
        destinataire.setPrenom( dto.getPrenom() );
        destinataire.setTelephone( dto.getTelephone() );

        return destinataire;
    }
}
