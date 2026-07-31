package com.via.shinviamock.account.dto.mydata;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepositTransactionItem {
    private String transDtime;
    private String transNo;
    private String transType;
    private String transClass;
    private String currencyCode;
    private BigDecimal transAmt;
    private BigDecimal balanceAmt;
    private Integer paidInCnt;
    private String transMemo;
    private String category;
}
