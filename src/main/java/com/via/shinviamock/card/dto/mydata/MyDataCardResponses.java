package com.via.shinviamock.card.dto.mydata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import java.util.List;

public final class MyDataCardResponses {
    private MyDataCardResponses() { }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class CardList {
        private String rspCode = "0000";
        private String rspMsg = "정상";
        private String searchTimestamp;
        private String nextPage;
        private int cardCnt;
        private List<CardItem> cardList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class CardBasic {
        private String rspCode = "0000";
        private String rspMsg = "정상";
        private String searchTimestamp;
        @JsonProperty("is_trans_payable")
        private boolean isTransPayable;
        @JsonProperty("is_cash_card")
        private boolean isCashCard;
        private String linkedBankCode;
        private String accountNum;
        private String cardBrand;
        private long annualFee;
        private java.time.LocalDate issueDate;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class BillList {
        private String rspCode = "0000";
        private String rspMsg = "정상";
        private String nextPage;
        private int billCnt;
        private List<CardBillItem> billList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class BillDetailList {
        private String rspCode = "0000";
        private String rspMsg = "정상";
        private String nextPage;
        private int billDetailCnt;
        private List<CardBillDetailItem> billDetailList;
    }
}
