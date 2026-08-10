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
import com.via.shinviamock.card.dto.mydata.CardBasicItem;
import com.via.shinviamock.card.dto.mydata.CardBillDetailItem;
import com.via.shinviamock.card.dto.mydata.CardBillItem;
import com.via.shinviamock.card.dto.mydata.CardItem;
import com.via.shinviamock.card.dto.mydata.MyDataCardResponses;

import java.util.List;


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

    public MyDataCardResponses.CardList getMyDataCards(String ci, String searchTimestamp, int limit) {
        List<CardItem> cards = mockCardMapper.selectMyDataCards(ci, limit + 1);
        MyDataCardResponses.CardList response = new MyDataCardResponses.CardList();
        response.setSearchTimestamp(searchTimestamp);
        response.setNextPage(trimAndHasNext(cards, limit));
        response.setCardCnt(cards.size());
        response.setCardList(cards);
        return response;
    }

    public MyDataCardResponses.CardBasic getMyDataCard(String ci, String cardId, String searchTimestamp) {
        CardBasicItem card = mockCardMapper.selectMyDataCard(ci, cardId);
        if (card == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "조회 권한이 없거나 존재하지 않는 card_id입니다.");
        }
        MyDataCardResponses.CardBasic response = new MyDataCardResponses.CardBasic();
        response.setSearchTimestamp(searchTimestamp);
        response.setTransPayable(card.isTransPayable());
        response.setCashCard(card.isCashCard());
        response.setLinkedBankCode(card.getLinkedBankCode());
        response.setAccountNum(card.getAccountNum());
        response.setCardBrand(card.getCardBrand());
        response.setAnnualFee(card.getAnnualFee());
        response.setIssueDate(card.getIssueDate());
        return response;
    }

    public MyDataCardResponses.BillList getMyDataBills(String ci, String fromMonth, String toMonth, int limit) {
        List<CardBillItem> bills = mockCardMapper.selectMyDataBills(ci, fromMonth, toMonth, limit + 1);
        MyDataCardResponses.BillList response = new MyDataCardResponses.BillList();
        response.setNextPage(trimAndHasNext(bills, limit));
        response.setBillCnt(bills.size());
        response.setBillList(bills);
        return response;
    }

    public MyDataCardResponses.BillDetailList getMyDataBillDetails(String ci, String chargeMonth, int limit) {
        List<CardBillDetailItem> details = mockCardMapper.selectMyDataBillDetails(ci, chargeMonth, limit + 1);
        MyDataCardResponses.BillDetailList response = new MyDataCardResponses.BillDetailList();
        response.setNextPage(trimAndHasNext(details, limit));
        response.setBillDetailCnt(details.size());
        response.setBillDetailList(details);
        return response;
    }

    private <T> String trimAndHasNext(List<T> rows, int limit) {
        if (rows.size() <= limit) return null;
        rows.remove(rows.size() - 1);
        return "1";
    }

    private void validateAuthorization(String authToken) {
        if (authToken == null || authToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization 헤더가 누락되었습니다.");
        }else if(redisTemplate.opsForValue().get(authToken) == null ){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "토큰과 일치하는 ci가 없습니다.");
        }
    }
}
