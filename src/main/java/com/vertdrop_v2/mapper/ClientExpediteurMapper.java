package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.ClientExpediteurDTO;
import com.vertdrop_v2.entity.ClientExpediteur;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientExpediteurMapper {

    @org.mapstruct.Mapping(source = "user.email", target = "email")
    @org.mapstruct.Mapping(target = "password", ignore = true)
    ClientExpediteurDTO toDto(ClientExpediteur entity);

    @org.mapstruct.Mapping(target = "user", ignore = true)
    ClientExpediteur toEntity(ClientExpediteurDTO dto);
}