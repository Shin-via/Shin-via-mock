package com.via.shinviamock.account.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.via.shinviamock.account.dto.mydata.DepositTransactionItem;
import lombok.Data;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepositTransactionsResponse {
    private String rspCode;
    private String rspMsg;
    private int transCnt;
    private List<DepositTransactionItem> transList;
}
