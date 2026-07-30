package com.via.shinviamock.account.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TransactionDto {

    private LocalDate tranDate;
    private LocalTime tranTime;
    private String inoutType;
    private String tranType;
    private String printedContent;
    private BigDecimal tranAmt;
    private BigDecimal afterBalanceAmt;
}
