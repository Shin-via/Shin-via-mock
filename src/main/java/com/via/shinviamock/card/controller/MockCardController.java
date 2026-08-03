package com.via.shinviamock.card.controller;

import com.via.shinviamock.card.dto.response.CardBillResponse;
import com.via.shinviamock.card.dto.response.CardListResponse;
import com.via.shinviamock.card.service.MockCardService;
import com.via.shinviamock.connection.service.MockTokenRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class MockCardController {

    private final MockCardService mockCardService;
    private final MockTokenRedisService mockTokenRedisService;

    @GetMapping("/v2.0/cards")
    public CardListResponse getCards(@RequestHeader("Authorization") String authorization,
                                      @RequestParam("bank_tran_id") String bankTranId,
                                      @RequestParam(value = "ci", required = false) String ci,
                                      @RequestParam("bank_code_std") String bankCodeStd,
                                      @RequestParam("member_bank_code") String memberBankCode,
                                      @RequestParam(value = "befor_inquiry_trace_info", required = false) String beforInquiryTraceInfo) {
        String targetCi = resolveCi(authorization, ci);
        return mockCardService.getCardList(bankTranId, targetCi, bankCodeStd, memberBankCode, beforInquiryTraceInfo);
    }

    @GetMapping("/v2.0/cards/bills")
    public CardBillResponse getCardBills(@RequestHeader("Authorization") String authorization,
                                          @RequestParam("bank_tran_id") String bankTranId,
                                          @RequestParam(value = "ci", required = false) String ci,
                                          @RequestParam("bank_code_std") String bankCodeStd,
                                          @RequestParam("member_bank_code") String memberBankCode,
                                          @RequestParam("from_month") String fromMonth,
                                          @RequestParam("to_month") String toMonth,
                                          @RequestParam(value = "befor_inquiry_trace_info", required = false) String beforInquiryTraceInfo) {
        String targetCi = resolveCi(authorization, ci);
        return mockCardService.getCardBills(bankTranId, targetCi, bankCodeStd, memberBankCode, fromMonth, toMonth, beforInquiryTraceInfo);
    }

    private String resolveCi(String authorization, String ciParam) {
        validateAuthorization(authorization);
        String tokenCi = mockTokenRedisService.getCiByAccessToken(authorization);
        if (tokenCi != null && !tokenCi.isBlank()) {
            return tokenCi;
        }
        if (ciParam != null && !ciParam.isBlank()) {
            return ciParam;
        }
        return "1"; // 기본 디폴트 CI
    }

    private void validateAuthorization(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization 헤더가 누락되었습니다.");
        }
    }
}
