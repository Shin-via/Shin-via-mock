package com.via.shinviamock.card.service;

import com.via.shinviamock.card.dto.response.CardBillResponse;
import com.via.shinviamock.card.dto.response.CardListResponse;
import com.via.shinviamock.card.mapper.MockCardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MockCardService {

    private final MockCardMapper mockCardMapper;

    public CardListResponse getCardList(String bankTranId, String userSeqNo, String bankCodeStd,
                                         String memberBankCode, String beforInquiryTraceInfo) {
       CardListResponse response = new CardListResponse();
        response.setSuccessHeader(bankTranId, bankCodeStd, userSeqNo, beforInquiryTraceInfo);
        response.setCardList(mockCardMapper.selectCardList(userSeqNo, bankCodeStd));
        response.setCardCnt(response.getCardList().size());
        return response;
    }

    public CardBillResponse getCardBills(String bankTranId, String userSeqNo, String bankCodeStd,
                                          String memberBankCode, String fromMonth, String toMonth,
                                          String beforInquiryTraceInfo) {

        CardBillResponse response = new CardBillResponse();
        response.setSuccessHeader(bankTranId, bankCodeStd, userSeqNo, beforInquiryTraceInfo);
        response.setBillList(mockCardMapper.selectCardBills(userSeqNo, bankCodeStd, fromMonth, toMonth));
        response.setBillCnt(response.getBillList().size());
        return response;
    }
}
