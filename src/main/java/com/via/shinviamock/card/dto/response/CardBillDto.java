package com.via.shinviamock.card.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CardBillDto {

    private String chargeMonth;
    private int settlementSeqNo;
    private String cardId;
    private long chargeAmt;
    private int settlementDay;
    private LocalDate settlementDate;
    private String creditCheckType;
}
