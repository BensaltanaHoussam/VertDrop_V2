package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.LivreurDTO;
import com.vertdrop_v2.entity.Livreur;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ZoneMapper.class)
public interface LivreurMapper {

    @org.mapstruct.Mapping(source = "user.email", target = "email")
    @org.mapstruct.Mapping(target = "password", ignore = true)
    LivreurDTO toDto(Livreur entity);

    @org.mapstruct.Mapping(target = "user", ignore = true)
    Livreur toEntity(LivreurDTO dto);
}
