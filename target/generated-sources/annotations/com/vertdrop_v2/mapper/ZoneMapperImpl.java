package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.ZoneDTO;
import com.vertdrop_v2.entity.Zone;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-19T12:03:23+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class ZoneMapperImpl implements ZoneMapper {

    @Override
    public ZoneDTO toDto(Zone entity) {
        if ( entity == null ) {
            return null;
        }

        ZoneDTO zoneDTO = new ZoneDTO();

        zoneDTO.setId( entity.getId() );
        zoneDTO.setNom( entity.getNom() );
        zoneDTO.setCodePostal( entity.getCodePostal() );

        return zoneDTO;
    }

    @Override
    public Zone toEntity(ZoneDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Zone zone = new Zone();

        zone.setId( dto.getId() );
        zone.setNom( dto.getNom() );
        zone.setCodePostal( dto.getCodePostal() );

        return zone;
    }
}
