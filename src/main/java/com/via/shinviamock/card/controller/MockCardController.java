package com.via.shinviamock.card.controller;

import com.via.shinviamock.card.dto.response.CardBillResponse;
import com.via.shinviamock.card.dto.response.CardListResponse;
import com.via.shinviamock.card.service.MockCardService;
import com.via.shinviamock.connection.service.MockTokenRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MockCardController {
    private final MockCardService mockCardService;
    private final MockTokenRedisService mockTokenRedisService;
    private final StringRedisTemplate redisTemplate;

    @GetMapping("/v2.0/cards")
    public CardListResponse getCards(@RequestHeader("Authorization") String authorization,
                                      @RequestHeader("x-api-tran-id") String xApiTranId,
                                      @RequestHeader("x-api-type") String xApiType,
                                      @RequestParam("org_code") String orgCode,
                                      @RequestParam("search_timestamp") String searchTimeStamp,
                                      @RequestParam(value = "next_page" ,required = false)String next_page,
                                      @RequestParam( "limit") int limit) {
        String targetCi = resolveCi(authorization);
        return mockCardService.getCardList( xApiTranId,targetCi);
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
        String targetCi = resolveCi(authorization);
        return mockCardService.getCardBills(bankTranId, targetCi, bankCodeStd, memberBankCode, fromMonth, toMonth, beforInquiryTraceInfo);
    }

    private String resolveCi(String authorization) {
        validateAuthorization(authorization);
        String cleanToken = authorization.startsWith("Bearer ") ? authorization.substring(7).trim() : authorization.trim();
        //레디스 에서 ci 데이터를 추출
        String tokenbuilder = "mydata:at:ci:" + cleanToken;
        String tokenCi = redisTemplate.opsForValue().get(tokenbuilder);
        if (tokenCi != null && !tokenCi.isBlank()) {
            return tokenCi;
        }
        return tokenCi;
    }

    private void validateAuthorization(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization 헤더가 누락되었습니다.");
        }
    }
}
