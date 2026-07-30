package com.via.shinviamock.account.dto.response;

import com.via.shinviamock.common.dto.CommonResponseHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class AccountBalanceResponse extends CommonResponseHeader {

    private String bankName;
    private BigDecimal balanceAmt;
    private BigDecimal availableAmt;
    private String accountType;
    private String productName;
}
