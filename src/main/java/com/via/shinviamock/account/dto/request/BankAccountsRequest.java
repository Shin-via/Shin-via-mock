package com.via.shinviamock.account.dto.request;

import lombok.Data;

@Data
public class BankAccountsRequest {
    private String authorization;
    private String xApiTranId;
    private String xApiType;
    private String orgCode;
    private String searchTimestamp;
    private String nextPage;
    private Integer limit;
}
