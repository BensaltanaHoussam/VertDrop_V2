package com.vertdrop_v2.controller;

import com.vertdrop_v2.dto.StatsSummaryDTO;
import com.vertdrop_v2.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "Endpoints for aggregate statistics and KPIs")
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/summary")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Get global statistics summary", description = "Returns counts and aggregates for the admin dashboard.")
    public ResponseEntity<StatsSummaryDTO> getSummary() {
        return ResponseEntity.ok(statsService.getSummary());
    }
}
