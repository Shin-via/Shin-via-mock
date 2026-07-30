package com.via.shinviamock.account.mapper;

import com.via.shinviamock.account.dto.response.AccountBalanceResponse;
import com.via.shinviamock.account.dto.response.TransactionDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MockAccountMapper {

    AccountBalanceResponse selectAccountByFintechUseNum(@Param("fintechUseNum") String fintechUseNum);

    List<TransactionDto> selectTransactions(@Param("fintechUseNum") String fintechUseNum,
                                             @Param("inquiryType") String inquiryType,
                                             @Param("inquiryBase") String inquiryBase,
                                             @Param("fromDate") String fromDate,
                                             @Param("fromTime") String fromTime,
                                             @Param("toDate") String toDate,
                                             @Param("toTime") String toTime,
                                             @Param("sortOrder") String sortOrder);
}
