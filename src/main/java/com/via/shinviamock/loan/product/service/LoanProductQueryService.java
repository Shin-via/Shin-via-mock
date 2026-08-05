package com.via.shinviamock.loan.product.service;

import com.via.shinviamock.loan.product.exception.LoanProductNotFoundException;
import com.via.shinviamock.loan.product.mapper.LoanProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;



// MockDB에 있는 대출 상품 조회하는 서비스단
// mapper
@Service
@RequiredArgsConstructor
public class LoanProductQueryService {

    private final LoanProductMapper mapper;

    // 전체 상품을 조회하거나 loanType/acctive 인 애들 조건 조회하기 위한 함수
    public List<Map<String, Object>> list(String loanType, Boolean active) {
        String normalized = loanType == null || loanType.isBlank()
                ? null
                : loanType.trim().toUpperCase();

        return mapper.findProductSummaries(normalized, active);
    }

    // 부모 상품 조회-> MORTGAGE/JEONSE 라면 주택 상세 옵션 조회
    // CREDIT 라면 신용 상세 옵션 조회
    // STUDENT 라면 학자금 상세 조회
    public Map<String, Object> detail(Long productId) {
        Map<String, Object> summary = mapper.findProductSummaryById(productId);
        if (summary == null) {
            throw new LoanProductNotFoundException(productId);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.putAll(summary);

        String loanType = String.valueOf(summary.get("loanType"));

        if ("MORTGAGE".equals(loanType) || "JEONSE".equals(loanType)) {
            result.put("detail", mapper.findHousingDetail(productId));
            result.put("options", mapper.findHousingOptions(productId));
        } else if ("CREDIT".equals(loanType)) {
            result.put("detail", mapper.findCreditDetail(productId));
            result.put("options", mapper.findCreditOptions(productId));
        } else if ("STUDENT".equals(loanType)) {
            result.put("detail", mapper.findStudentDetail(productId));
            result.put("options", List.of());
        }

        return result;
    }
}
