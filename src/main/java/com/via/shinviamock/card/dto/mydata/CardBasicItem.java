package com.via.shinviamock.card.dto.mydata;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.time.LocalDate;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class CardBasicItem {
    @JsonProperty("is_trans_payable")
    private boolean isTransPayable;
    @JsonProperty("is_cash_card")
    private boolean isCashCard;
    private String linkedBankCode;
    private String accountNum;
    private String cardBrand;
    private long annualFee;
    private LocalDate issueDate;
}
