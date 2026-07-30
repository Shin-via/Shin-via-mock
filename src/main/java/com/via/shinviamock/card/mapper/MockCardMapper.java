package com.via.shinviamock.card.mapper;

import com.via.shinviamock.card.dto.response.CardBillDto;
import com.via.shinviamock.card.dto.response.CardInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MockCardMapper {

    List<CardInfoDto> selectCardList(@Param("userSeqNo") String userSeqNo,
                                      @Param("bankCodeStd") String bankCodeStd);

    List<CardBillDto> selectCardBills(@Param("userSeqNo") String userSeqNo,
                                       @Param("bankCodeStd") String bankCodeStd,
                                       @Param("fromMonth") String fromMonth,
                                       @Param("toMonth") String toMonth);
}
