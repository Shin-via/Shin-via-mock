package com.via.shinviamock.card.dto.mydata;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CardBillItem {
    private String seqno;
    private long chargeAmt;
    private String chargeDay;
    private String chargeMonth;
    private LocalDate paidOutDate;
}
