package com.via.shinviamock.account.dto.request;

import lombok.Data;

@Data
public class DepositTransactionsRequest {
    private String orgCode;
    private String accountNum;
    private String seqno;
    private String fromDate;
    private String toDate;
    private Integer limit;
}
