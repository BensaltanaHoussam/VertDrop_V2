package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.LivreurDTO;
import com.vertdrop_v2.entity.Livreur;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ZoneMapper.class)
public interface LivreurMapper {

    LivreurDTO toDto(Livreur entity);

    Livreur toEntity(LivreurDTO dto);
}
