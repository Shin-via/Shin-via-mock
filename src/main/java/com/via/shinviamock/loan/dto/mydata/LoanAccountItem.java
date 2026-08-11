package com.via.shinviamock.loan.dto.mydata;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// service db에 내려줄 dto
@Data
public class LoanAccountItem {

    private String externalLoanKey;

    private String loanType;

    private BigDecimal principalAmount;

    private BigDecimal currentBalance;

    private BigDecimal interestRate;

    private String rateType;

    private String repaymentType;

    private LocalDate disbursedAt;

    private LocalDate maturityAt;

    private String loanStatus;

    private LocalDateTime dataAsOfAt;

    private BigDecimal prepaymentFeeRate;

    private LocalDate prepaymentFeeEndDate;
}