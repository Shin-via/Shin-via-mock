package com.via.shinviamock.loan.product.controller;

import com.via.shinviamock.loan.product.client.FinlifeLoanProductClient;
import com.via.shinviamock.loan.product.dto.external.mortgage.MortgageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/loan-products/test")
@RequiredArgsConstructor
public class LoanProductTestController {

    private final FinlifeLoanProductClient client;

    @GetMapping("/mortgage")
    public Map<String, Object> testMortgage(
            @RequestParam(defaultValue = "1")
            int pageNo
    ) {
        MortgageResponse response =
                client.fetchMortgage(pageNo);

        if (response == null
                || response.result() == null) {
            throw new IllegalStateException(
                    "금감원 주담대 API 응답이 비어 있습니다."
            );
        }

        MortgageResponse.Result result =
                response.result();

        Map<String, Object> body =
                new LinkedHashMap<>();

        body.put("errorCode", result.errorCode());
        body.put("errorMessage", result.errorMessage());
        body.put("nowPageNo", result.nowPageNo());
        body.put("maxPageNo", result.maxPageNo());
        body.put("totalCount", result.totalCount());

        body.put(
                "baseCount",
                result.baseList() == null
                        ? 0
                        : result.baseList().size()
        );

        body.put(
                "optionCount",
                result.optionList() == null
                        ? 0
                        : result.optionList().size()
        );

        if (result.baseList() != null
                && !result.baseList().isEmpty()) {

            MortgageResponse.Base first =
                    result.baseList().get(0);

            body.put("firstProductName", first.productName());
            body.put(
                    "firstInstitutionName",
                    first.institutionName()
            );
        }

        return body;
    }
}