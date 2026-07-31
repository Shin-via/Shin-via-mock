package com.via.shinviamock.account.dto.request;

import lombok.Data;

@Data
public class DepositBasicRequest {
    private String orgCode;
    private String accountNum;
    private String seqno;
    private String searchTimestamp;
}
