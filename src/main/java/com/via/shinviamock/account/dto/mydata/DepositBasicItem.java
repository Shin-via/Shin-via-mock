package com.via.shinviamock.account.dto.mydata;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepositBasicItem {
    private String currencyCode;
    private String savingMethod;
    private String issueDate;
    private String expDate;
    private BigDecimal commitAmt;
    private BigDecimal monthlyPaidInAmt;
}
