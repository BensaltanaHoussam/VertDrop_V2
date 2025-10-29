package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.LivreurDTO;
import com.vertdrop_v2.entity.Livreur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ZoneMapper.class)
public interface LivreurMapper {

    // Note: The user's LivreurDTO doesn't have a zone field.
    // If you add a ZoneDTO to LivreurDTO, this mapping will work automatically.
    // For now, it will be ignored.
    LivreurDTO toDto(Livreur entity);

    Livreur toEntity(LivreurDTO dto);
}