package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.DestinataireDTO;
import com.vertdrop_v2.entity.Destinataire;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DestinataireMapper {

    DestinataireDTO toDto(Destinataire entity);

    Destinataire toEntity(DestinataireDTO dto);
}