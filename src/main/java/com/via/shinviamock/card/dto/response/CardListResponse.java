package com.via.shinviamock.card.dto.response;

import com.via.shinviamock.common.dto.CommonResponseHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CardListResponse extends CommonResponseHeader {

    private int cardCnt;
    private List<CardInfoDto> cardList;
}
