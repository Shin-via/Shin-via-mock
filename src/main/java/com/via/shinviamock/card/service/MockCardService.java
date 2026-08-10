package com.via.shinviamock.card.service;

import com.via.shinviamock.card.dto.response.CardBillResponse;
import com.via.shinviamock.card.dto.response.CardListResponse;
import com.via.shinviamock.card.mapper.MockCardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class MockCardService {

    private final MockCardMapper mockCardMapper;
    private final StringRedisTemplate redisTemplate;

    public CardListResponse getCardList(String xApiTranId, String AuthToken ) {
       CardListResponse response = new CardListResponse();
        response.setSucccessHeaderForList(xApiTranId);
        response.setCardList(mockCardMapper.selectCardList(AuthToken));
        response.setCardCnt(response.getCardList().size());
        return response;
    }

    public CardBillResponse getCardBills(String bankTranId, String ci, String bankCodeStd,
                                          String memberBankCode, String fromMonth, String toMonth,
                                          String beforInquiryTraceInfo) {
        CardBillResponse response = new CardBillResponse();
        response.setSuccessHeader(bankTranId, bankCodeStd, ci, beforInquiryTraceInfo);
        response.setBillList(mockCardMapper.selectCardBills(ci, bankCodeStd, fromMonth, toMonth));
        response.setBillCnt(response.getBillList().size());
        return response;
    }
    public void AuthTokenValidation(String AuthToken){
        validateAuthorization(AuthToken);
    }

    private void validateAuthorization(String authToken) {
        if (authToken == null || authToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization 헤더가 누락되었습니다.");
        }else if(redisTemplate.opsForValue().get(authToken) == null ){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "토큰과 일치하는 ci가 없습니다.");
        }
    }
}
