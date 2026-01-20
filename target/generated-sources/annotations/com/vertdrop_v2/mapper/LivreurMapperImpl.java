package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.LivreurDTO;
import com.vertdrop_v2.entity.Livreur;
import com.vertdrop_v2.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-20T10:28:35+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class LivreurMapperImpl implements LivreurMapper {

    @Autowired
    private ZoneMapper zoneMapper;

    @Override
    public LivreurDTO toDto(Livreur entity) {
        if ( entity == null ) {
            return null;
        }

        LivreurDTO livreurDTO = new LivreurDTO();

        livreurDTO.setEmail( entityUserEmail( entity ) );
        livreurDTO.setId( entity.getId() );
        livreurDTO.setNom( entity.getNom() );
        livreurDTO.setPrenom( entity.getPrenom() );
        livreurDTO.setTelephone( entity.getTelephone() );
        livreurDTO.setVehicule( entity.getVehicule() );
        livreurDTO.setZoneAssignee( zoneMapper.map( entity.getZoneAssignee() ) );

        return livreurDTO;
    }

    @Override
    public Livreur toEntity(LivreurDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Livreur livreur = new Livreur();

        livreur.setId( dto.getId() );
        livreur.setNom( dto.getNom() );
        livreur.setPrenom( dto.getPrenom() );
        livreur.setTelephone( dto.getTelephone() );
        livreur.setVehicule( dto.getVehicule() );
        livreur.setZoneAssignee( zoneMapper.map( dto.getZoneAssignee() ) );

        return livreur;
    }

    private String entityUserEmail(Livreur livreur) {
        if ( livreur == null ) {
            return null;
        }
        User user = livreur.getUser();
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
