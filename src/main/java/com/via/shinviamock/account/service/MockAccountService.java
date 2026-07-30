package com.via.shinviamock.account.service;

import com.via.shinviamock.account.dto.response.AccountBalanceResponse;
import com.via.shinviamock.account.dto.response.AccountTransactionResponse;
import com.via.shinviamock.account.dto.response.TransactionDto;
import com.via.shinviamock.account.mapper.MockAccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MockAccountService {

    private final MockAccountMapper mockAccountMapper;

    public AccountBalanceResponse getAccountBalance(String bankTranId, String fintechUseNum, String bankCodeStd) {
        AccountBalanceResponse response = mockAccountMapper.selectAccountByFintechUseNum(fintechUseNum);
        String userSeqNo = response.getUserSeqNo();
        response.setSuccessHeader(bankTranId, bankCodeStd, userSeqNo, null);
        return response;
    }

    public AccountTransactionResponse getAccountTransactions(String bankTranId, String fintechUseNum, String inquiryType,
                                                              String inquiryBase, String fromDate, String fromTime,
                                                              String toDate, String toTime, String sortOrder,
                                                              String tranDtime, String beforInquiryTraceInfo) {
        // tran_dtime(요청일시)은 요청 메타데이터일 뿐 조회 조건으로 사용하지 않음
        AccountBalanceResponse accountInfo = mockAccountMapper.selectAccountByFintechUseNum(fintechUseNum);
        List<TransactionDto> transactions = mockAccountMapper.selectTransactions(
                fintechUseNum, inquiryType, inquiryBase, fromDate, fromTime, toDate, toTime, sortOrder);

        AccountTransactionResponse response = new AccountTransactionResponse();
        response.setSuccessHeader(bankTranId, accountInfo.getBankCodeTran(), accountInfo.getUserSeqNo(), beforInquiryTraceInfo);
        response.setBalanceAmt(accountInfo.getBalanceAmt());
        response.setResList(transactions);
        response.setPageRecordCnt(transactions.size());
        return response;
    }
}
