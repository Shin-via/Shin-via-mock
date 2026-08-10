package com.via.shinviamock.card.controller;

import com.via.shinviamock.card.dto.response.CardBillResponse;
import com.via.shinviamock.card.dto.response.CardListResponse;
import com.via.shinviamock.card.service.MockCardService;
import com.via.shinviamock.connection.service.MockTokenRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.via.shinviamock.card.dto.mydata.MyDataCardResponses;

import java.util.regex.Pattern;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MockCardController {

    private static final Pattern MONTH_PATTERN = Pattern.compile("\\d{6}");

    private final MockCardService mockCardService;
    private final MockTokenRedisService mockTokenRedisService;

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

    /** 마이데이터 표준 카드-001: 보유 카드 목록. */
    @GetMapping("/v2/card/cards")
    public MyDataCardResponses.CardList getMyDataCards(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam("org_code") String orgCode,
            @RequestParam(value = "search_timestamp", required = false, defaultValue = "") String searchTimestamp,
            @RequestParam(value = "next_page", required = false) String nextPage,
            @RequestParam("limit") int limit) {
        return mockCardService.getMyDataCards(resolveCi(authorization), searchTimestamp, validateLimit(limit));
    }

    /** 마이데이터 표준 카드-002: 카드 기본 정보. */
    @GetMapping("/v2/card/cards/{cardId}")
    public MyDataCardResponses.CardBasic getMyDataCard(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @org.springframework.web.bind.annotation.PathVariable String cardId,
            @RequestParam("org_code") String orgCode,
            @RequestParam("search_timestamp") String searchTimestamp) {
        return mockCardService.getMyDataCard(resolveCi(authorization), cardId, searchTimestamp);
    }

//   마이데이터 표준 카드-004: 고객 단위 월별 청구 합산.
    @GetMapping("/v2/card/bills")
    public MyDataCardResponses.BillList getMyDataBills(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam("org_code") String orgCode,
            @RequestParam("from_month") String fromMonth,
            @RequestParam("to_month") String toMonth,
            @RequestParam(value = "next_page", required = false) String nextPage,
            @RequestParam("limit") int limit) {
        validateMonth(fromMonth, "from_month");
        validateMonth(toMonth, "to_month");
        if (fromMonth.compareTo(toMonth) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "from_month는 to_month보다 클 수 없습니다.");
        }
        return mockCardService.getMyDataBills(resolveCi(authorization), fromMonth, toMonth, validateLimit(limit));
    }

//    /마이데이터 표준 카드-005: 월별 청구 상세 거래 내역.
    @GetMapping("/v2/card/bills/detail")
    public MyDataCardResponses.BillDetailList getMyDataBillDetails(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam("org_code") String orgCode,
            @RequestParam(value = "seqno", required = false) String seqno,
            @RequestParam("charge_month") String chargeMonth,
            @RequestParam(value = "next_page", required = false) String nextPage,
            @RequestParam("limit") int limit) {
        validateMonth(chargeMonth, "charge_month");
        return mockCardService.getMyDataBillDetails(resolveCi(authorization), chargeMonth, validateLimit(limit));
    }

    private String resolveCi(String authorization) {
        validateAuthorization(authorization);
        String cleanToken = authorization.startsWith("Bearer ") ? authorization.substring(7).trim() : authorization.trim();
        String tokenCi = mockTokenRedisService.getCiByAccessToken(cleanToken);
        if (tokenCi != null && !tokenCi.isBlank()) {
            return tokenCi;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않거나 만료된 access token입니다.");
    }

    private void validateAuthorization(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization 헤더가 누락되었습니다.");
        }
    }

    private int validateLimit(int limit) {
        if (limit < 1 || limit > 500) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "limit은 1 이상 500 이하여야 합니다.");
        }
        return limit;
    }

    private void validateMonth(String month, String parameterName) {
        if (month == null || !MONTH_PATTERN.matcher(month).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, parameterName + "은 YYYYMM 형식이어야 합니다.");
        }
    }
}
