package com.via.shinviamock.card.dto.response;

import com.via.shinviamock.common.dto.CommonResponseHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CardBillResponse extends CommonResponseHeader {

    private int billCnt;
    private List<CardBillDto> billList;
}
