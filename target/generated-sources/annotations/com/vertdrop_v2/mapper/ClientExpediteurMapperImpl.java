package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.ClientExpediteurDTO;
import com.vertdrop_v2.entity.ClientExpediteur;
import com.vertdrop_v2.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-20T09:19:39+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260101-2150, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class ClientExpediteurMapperImpl implements ClientExpediteurMapper {

    @Override
    public ClientExpediteurDTO toDto(ClientExpediteur entity) {
        if ( entity == null ) {
            return null;
        }

        ClientExpediteurDTO clientExpediteurDTO = new ClientExpediteurDTO();

        clientExpediteurDTO.setEmail( entityUserEmail( entity ) );
        clientExpediteurDTO.setAdresse( entity.getAdresse() );
        clientExpediteurDTO.setId( entity.getId() );
        clientExpediteurDTO.setNom( entity.getNom() );
        clientExpediteurDTO.setPrenom( entity.getPrenom() );
        clientExpediteurDTO.setTelephone( entity.getTelephone() );

        return clientExpediteurDTO;
    }

    @Override
    public ClientExpediteur toEntity(ClientExpediteurDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ClientExpediteur clientExpediteur = new ClientExpediteur();

        clientExpediteur.setAdresse( dto.getAdresse() );
        clientExpediteur.setEmail( dto.getEmail() );
        clientExpediteur.setId( dto.getId() );
        clientExpediteur.setNom( dto.getNom() );
        clientExpediteur.setPrenom( dto.getPrenom() );
        clientExpediteur.setTelephone( dto.getTelephone() );

        return clientExpediteur;
    }

    private String entityUserEmail(ClientExpediteur clientExpediteur) {
        if ( clientExpediteur == null ) {
            return null;
        }
        User user = clientExpediteur.getUser();
        if ( user == null ) {
            return null;
        }
        String email = user.getEmail();
        if ( email == null ) {
            return null;
        }
        return email;
    }
}
