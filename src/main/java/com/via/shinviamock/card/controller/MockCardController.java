package com.via.shinviamock.card.controller;

import com.via.shinviamock.card.dto.response.CardBillResponse;
import com.via.shinviamock.card.dto.response.CardListResponse;
import com.via.shinviamock.card.service.MockCardService;
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

    @GetMapping("/v2.0/cards")
    public CardListResponse getCards(@RequestHeader("Authorization") String authorization,
                                      @RequestParam("bank_tran_id") String bankTranId,
                                      @RequestParam("user_seq_no") String userSeqNo,
                                      @RequestParam("bank_code_std") String bankCodeStd,
                                      @RequestParam("member_bank_code") String memberBankCode,
                                      @RequestParam(value = "befor_inquiry_trace_info", required = false) String beforInquiryTraceInfo) {
        validateAuthorization(authorization);
        return mockCardService.getCardList(bankTranId, userSeqNo, bankCodeStd, memberBankCode, beforInquiryTraceInfo);
    }

    @GetMapping("/v2.0/cards/bills")
    public CardBillResponse getCardBills(@RequestHeader("Authorization") String authorization,
                                          @RequestParam("bank_tran_id") String bankTranId,
                                          @RequestParam("user_seq_no") String userSeqNo,
                                          @RequestParam("bank_code_std") String bankCodeStd,
                                          @RequestParam("member_bank_code") String memberBankCode,
                                          @RequestParam("from_month") String fromMonth,
                                          @RequestParam("to_month") String toMonth,
                                          @RequestParam(value = "befor_inquiry_trace_info", required = false) String beforInquiryTraceInfo) {
        validateAuthorization(authorization);
        return mockCardService.getCardBills(bankTranId, userSeqNo, bankCodeStd, memberBankCode, fromMonth, toMonth, beforInquiryTraceInfo);
    }

    private void validateAuthorization(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header must be in 'Bearer <token>' format");
        }
    }
}
