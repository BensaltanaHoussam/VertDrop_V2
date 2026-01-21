package com.vertdrop_v2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsSummaryDTO {
    private long totalColis;
    private long totalLivreurs;
    private long totalClients;
    private BigDecimal totalWeight;
    private Map<String, Long> countByStatus;
}
