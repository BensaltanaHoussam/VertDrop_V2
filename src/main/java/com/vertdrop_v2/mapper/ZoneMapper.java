package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.ZoneDTO;
import com.vertdrop_v2.entity.Zone;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ZoneMapper {

    ZoneDTO toDto(Zone entity);

    Zone toEntity(ZoneDTO dto);

    // Méthodes utilitaires utilisées par d'autres mappers (ex : conversion Zone <-> id)
    default Long map(Zone zone) {
        return zone == null ? null : zone.getId();
    }

    default Zone map(Long id) {
        if (id == null) {
            return null;
        }
        Zone zone = new Zone();
        zone.setId(id);
        return zone;
    }
}
