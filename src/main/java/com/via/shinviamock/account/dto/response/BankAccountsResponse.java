package com.via.shinviamock.account.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.via.shinviamock.account.dto.mydata.AccountItem;
import lombok.Data;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BankAccountsResponse {
    private String rspCode;
    private String rspMsg;
    private String searchTimestamp;
    private String regDate;
    private String nextPage;
    private int accountCnt;
    private List<AccountItem> accountList;
}
