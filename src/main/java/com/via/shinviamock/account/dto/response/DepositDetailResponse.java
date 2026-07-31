package com.via.shinviamock.account.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.via.shinviamock.account.dto.mydata.DepositDetailItem;
import lombok.Data;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepositDetailResponse {
    private String rspCode;
    private String rspMsg;
    private String searchTimestamp;
    private int detailCnt;
    private List<DepositDetailItem> detailList;
}
