package com.via.shinviamock.card.mapper;

import com.via.shinviamock.card.dto.response.CardBillDto;
import com.via.shinviamock.card.dto.response.CardInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MockCardMapper {

    List<CardInfoDto> selectCardList(@Param("ci") String ci);
    List<CardInfoDto> selectCardListByBank(@Param("ci") String ci,
                                     @Param("bankCodeStd") String bankCodeStd);
    List<CardBillDto> selectCardBills(@Param("ci") String ci,
                                       @Param("bankCodeStd") String bankCodeStd,
                                       @Param("fromMonth") String fromMonth,
                                       @Param("toMonth") String toMonth);

    List<com.via.shinviamock.card.dto.mydata.CardItem> selectMyDataCards(@Param("ci") String ci,
                                                                           @Param("limit") int limit);

    com.via.shinviamock.card.dto.mydata.CardBasicItem selectMyDataCard(@Param("ci") String ci,
                                                                         @Param("cardId") String cardId);

    List<com.via.shinviamock.card.dto.mydata.CardBillItem> selectMyDataBills(@Param("ci") String ci,
                                                                                @Param("fromMonth") String fromMonth,
                                                                                @Param("toMonth") String toMonth,
                                                                                @Param("limit") int limit);

    List<com.via.shinviamock.card.dto.mydata.CardBillDetailItem> selectMyDataBillDetails(@Param("ci") String ci,
                                                                                            @Param("chargeMonth") String chargeMonth,
                                                                                            @Param("limit") int limit);
}
