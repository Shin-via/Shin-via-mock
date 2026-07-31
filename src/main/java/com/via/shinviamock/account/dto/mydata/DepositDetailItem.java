package com.via.shinviamock.account.dto.mydata;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepositDetailItem {
    private String currencyCode;
    private BigDecimal balanceAmt;
    private BigDecimal withdrawableAmt;
    private BigDecimal offeredRate;
    private Integer lastPaidInCnt;
}
