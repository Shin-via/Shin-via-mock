package com.via.shinviamock.card.dto.mydata;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CardBillDetailItem {
    private String cardId;
    private String paidDtime;
    private String transNo;
    private BigDecimal paidAmt;
    private String currencyCode;
    private String merchantName;
    private String merchantRegno;
    private long creditFeeAmt;
    private Integer totalInstallCnt;
    private Integer curInstallCnt;
    private Long balanceAmt;
    private String prodType;
}
