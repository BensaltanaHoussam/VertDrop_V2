package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.ClientExpediteurDTO;
import com.vertdrop_v2.entity.ClientExpediteur;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientExpediteurMapper {

    ClientExpediteurDTO toDto(ClientExpediteur entity);

    ClientExpediteur toEntity(ClientExpediteurDTO dto);
}