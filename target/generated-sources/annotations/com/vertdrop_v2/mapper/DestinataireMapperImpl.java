package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.DestinataireDTO;
import com.vertdrop_v2.entity.Destinataire;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-20T10:28:36+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class DestinataireMapperImpl implements DestinataireMapper {

    @Override
    public DestinataireDTO toDto(Destinataire entity) {
        if ( entity == null ) {
            return null;
        }

        DestinataireDTO destinataireDTO = new DestinataireDTO();

        destinataireDTO.setId( entity.getId() );
        destinataireDTO.setNom( entity.getNom() );
        destinataireDTO.setPrenom( entity.getPrenom() );
        destinataireDTO.setEmail( entity.getEmail() );
        destinataireDTO.setTelephone( entity.getTelephone() );
        destinataireDTO.setAdresse( entity.getAdresse() );

        return destinataireDTO;
    }

    @Override
    public Destinataire toEntity(DestinataireDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Destinataire destinataire = new Destinataire();

        destinataire.setId( dto.getId() );
        destinataire.setNom( dto.getNom() );
        destinataire.setPrenom( dto.getPrenom() );
        destinataire.setEmail( dto.getEmail() );
        destinataire.setTelephone( dto.getTelephone() );
        destinataire.setAdresse( dto.getAdresse() );

        return destinataire;
    }
}
