package com.via.shinviamock.account.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.via.shinviamock.account.dto.mydata.DepositBasicItem;
import lombok.Data;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepositBasicResponse {
    private String rspCode;
    private String rspMsg;
    private String searchTimestamp;
    private int basicCnt;
    private List<DepositBasicItem> basicList;
}
