package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.ZoneDTO;
import com.vertdrop_v2.entity.Zone;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-21T09:23:36+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260101-2150, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class ZoneMapperImpl implements ZoneMapper {

    @Override
    public ZoneDTO toDto(Zone entity) {
        if ( entity == null ) {
            return null;
        }

        ZoneDTO zoneDTO = new ZoneDTO();

        zoneDTO.setCodePostal( entity.getCodePostal() );
        zoneDTO.setId( entity.getId() );
        zoneDTO.setNom( entity.getNom() );

        return zoneDTO;
    }

    @Override
    public Zone toEntity(ZoneDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Zone zone = new Zone();

        zone.setCodePostal( dto.getCodePostal() );
        zone.setId( dto.getId() );
        zone.setNom( dto.getNom() );

        return zone;
    }
}
