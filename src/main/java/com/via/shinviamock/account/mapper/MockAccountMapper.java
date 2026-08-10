package com.via.shinviamock.account.mapper;

import com.via.shinviamock.account.dto.response.AccountBalanceResponse;
import com.via.shinviamock.account.dto.response.TransactionDto;
import com.via.shinviamock.account.dto.mydata.AccountItem;
import com.via.shinviamock.account.dto.mydata.DepositBasicItem;
import com.via.shinviamock.account.dto.mydata.DepositDetailItem;
import com.via.shinviamock.account.dto.mydata.DepositTransactionItem;
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

    List<AccountItem> selectMyDataAccounts(@Param("ci") int ci, @Param("limit") int limit,
                                            @Param("offset") int offset);
    int countMyDataAccounts(@Param("orgCode") String orgCode);
    String selectMyDataRegDate(@Param("orgCode") String orgCode);
    List<DepositBasicItem> selectDepositBasics(@Param("orgCode") String orgCode, @Param("accountNum") String accountNum,
                                                @Param("seqno") String seqno);
    List<DepositDetailItem> selectDepositDetails(@Param("orgCode") String orgCode, @Param("accountNum") String accountNum,
                                                  @Param("seqno") String seqno);
    List<DepositTransactionItem> selectDepositTransactions(@Param("orgCode") String orgCode, @Param("accountNum") String accountNum,
                                                            @Param("seqno") String seqno, @Param("fromDate") String fromDate,
                                                            @Param("toDate") String toDate, @Param("limit") int limit,
                                                            @Param("offset") int offset);
}
