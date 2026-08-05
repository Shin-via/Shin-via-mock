package com.via.shinviamock.loan.product.controller;

import com.via.shinviamock.loan.product.dto.response.LoanProductResponses;
import com.via.shinviamock.loan.product.service.LoanProductQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// 우리 서비스에서 밑 주소로 호출하면 대출 상품들을 긁어옵니다
@RestController
@RequestMapping("/v2/loan-products")
@RequiredArgsConstructor
public class LoanProductController {

    private final LoanProductQueryService service;


    /**
     * 전체 대출상품 목록 조회
     *
     * GET /v2/loan-products
     * GET /v2/loan-products?loan_type=CREDIT
     * GET /v2/loan-products?loan_type=JEONSE&active=true
     */
    @GetMapping
    public LoanProductResponses.ListEnvelope list(
            @RequestParam(
                    name = "loan_type",
                    required = false
            )
            String loanType,
            @RequestParam(
                    name = "active",
                    defaultValue = "true"
            )
            Boolean active
    ) {
        return LoanProductResponses.ListEnvelope.success(
                service.list(loanType, active)
        );
    }


    @GetMapping("/{loanProductId}")
    public LoanProductResponses.DetailEnvelope detail(
            @PathVariable Long loanProductId
    ) {
        return LoanProductResponses.DetailEnvelope.success(
                service.detail(loanProductId)
        );
    }
}
