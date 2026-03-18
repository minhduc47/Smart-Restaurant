package com.minhduc.smartrestaurant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minhduc.smartrestaurant.domain.response.ResDashboardDTO;
import com.minhduc.smartrestaurant.service.DashboardService;
import com.minhduc.smartrestaurant.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    @PreAuthorize("@dashboardService.isCurrentUserAdmin()")
    @ApiMessage("Fetch dashboard statistics")
    public ResponseEntity<ResDashboardDTO> getDashboardStats() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }
}