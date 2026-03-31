package com.volpe.controller;

import com.volpe.dto.RevenueRequest;
import com.volpe.dto.RevenueResponse;
import com.volpe.service.RevenueService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/revenue")
public class RevenueController {

    private final RevenueService revenueService;

    public RevenueController(final RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    @GetMapping
    public RevenueResponse getRevenue(@RequestBody @Valid final RevenueRequest request) {
        return revenueService.getRevenue(request);
    }

}
