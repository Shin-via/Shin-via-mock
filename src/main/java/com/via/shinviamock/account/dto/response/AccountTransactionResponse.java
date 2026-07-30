package com.via.shinviamock.account.dto.response;

import com.via.shinviamock.common.dto.CommonResponseHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AccountTransactionResponse extends CommonResponseHeader {

    private BigDecimal balanceAmt;
    private int pageRecordCnt;
    private List<TransactionDto> resList;
}
