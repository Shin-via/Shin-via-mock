package com.via.shinviamock.account.dto.mydata;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountItem {
    private String accountNum;
    private Boolean isConsent;
    private String seqno;
    private Boolean isForeignDeposit;
    private String prodName;
    private Boolean isMinus;
    private String accountType;
    private String accountStatus;
}
