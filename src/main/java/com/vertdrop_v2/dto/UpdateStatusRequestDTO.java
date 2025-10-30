package com.vertdrop_v2.dto;

import com.vertdrop_v2.entity.StatutColis;
import lombok.Data;

@Data
public class UpdateStatusRequestDTO {
    private StatutColis newStatus;
    private String comment;
}