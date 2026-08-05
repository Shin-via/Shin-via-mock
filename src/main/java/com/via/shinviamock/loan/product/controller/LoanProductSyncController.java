package com.via.shinviamock.loan.product.controller;

import com.via.shinviamock.loan.product.dto.response.LoanProductResponses;
import com.via.shinviamock.loan.product.service.LoanProductSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


// postman 에 이 주소 기준으로 던지면 가능
@RestController
@RequestMapping("/api/admin/loan-products/sync")
@RequiredArgsConstructor
public class LoanProductSyncController {

    private final LoanProductSyncService service;

    @PostMapping("/mortgage")
    public LoanProductResponses.SyncResult mortgage() {
        return service.mortgage();
    }

    @PostMapping("/jeonse")
    public LoanProductResponses.SyncResult jeonse() {
        return service.jeonse();
    }

    @PostMapping("/credit")
    public LoanProductResponses.SyncResult credit() {
        return service.credit();
    }

    @PostMapping("/finlife")
    public List<LoanProductResponses.SyncResult> allFinlife() {
        return service.allFinlife();
    }
}
