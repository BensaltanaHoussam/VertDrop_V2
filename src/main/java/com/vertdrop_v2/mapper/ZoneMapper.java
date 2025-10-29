package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.ZoneDTO;
import com.vertdrop_v2.entity.Zone;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ZoneMapper {

    ZoneDTO toDto(Zone entity);

    Zone toEntity(ZoneDTO dto);
}