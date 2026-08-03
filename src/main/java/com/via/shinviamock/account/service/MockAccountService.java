package com.via.shinviamock.account.service;

import com.via.shinviamock.account.dto.response.AccountBalanceResponse;
import com.via.shinviamock.account.dto.response.AccountTransactionResponse;
import com.via.shinviamock.account.dto.response.TransactionDto;
import com.via.shinviamock.account.dto.request.*;
import com.via.shinviamock.account.dto.response.*;
import com.via.shinviamock.account.dto.mydata.*;
import com.via.shinviamock.account.mapper.MockAccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MockAccountService {

    private final MockAccountMapper mockAccountMapper;

    public AccountBalanceResponse getAccountBalance(String bankTranId, String fintechUseNum, String bankCodeStd) {
        AccountBalanceResponse response = mockAccountMapper.selectAccountByFintechUseNum(fintechUseNum);
        if (response == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found for fintech_use_num: " + fintechUseNum);
        }
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
        if (accountInfo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found for fintech_use_num: " + fintechUseNum);
        }
        List<TransactionDto> transactions = mockAccountMapper.selectTransactions(
                fintechUseNum, inquiryType, inquiryBase, fromDate, fromTime, toDate, toTime, sortOrder);

        AccountTransactionResponse response = new AccountTransactionResponse();
        response.setSuccessHeader(bankTranId, accountInfo.getBankCodeTran(), accountInfo.getUserSeqNo(), beforInquiryTraceInfo);
        response.setBalanceAmt(accountInfo.getBalanceAmt());
        response.setResList(transactions);
        response.setPageRecordCnt(transactions != null ? transactions.size() : 0);
        return response;
    }

    public BankAccountsResponse getMyDataAccounts(BankAccountsRequest request) {
        int offset = pageOffset(request.getNextPage());
        List<AccountItem> accounts = mockAccountMapper.selectMyDataAccounts(request.getOrgCode(), request.getLimit() + 1, offset);
        BankAccountsResponse response = success(new BankAccountsResponse());
        response.setSearchTimestamp("0");
        response.setRegDate(mockAccountMapper.selectMyDataRegDate(request.getOrgCode()));
        response.setAccountCnt(mockAccountMapper.countMyDataAccounts(request.getOrgCode()));
        setNextPageIfPresent(accounts, request.getLimit(), offset, response::setNextPage);
        response.setAccountList(accounts);
        return response;
    }

    public DepositBasicResponse getDepositBasics(DepositBasicRequest request) {
        List<DepositBasicItem> basics = mockAccountMapper.selectDepositBasics(request.getOrgCode(), request.getAccountNum(), request.getSeqno());
        DepositBasicResponse response = success(new DepositBasicResponse());
        response.setSearchTimestamp("0");
        response.setBasicCnt(basics.size());
        response.setBasicList(basics);
        return response;
    }

    public DepositDetailResponse getDepositDetails(DepositDetailRequest request) {
        List<DepositDetailItem> details = mockAccountMapper.selectDepositDetails(request.getOrgCode(), request.getAccountNum(), request.getSeqno());
        DepositDetailResponse response = success(new DepositDetailResponse());
        response.setSearchTimestamp("0");
        response.setDetailCnt(details.size());
        response.setDetailList(details);
        return response;
    }

    public DepositTransactionsResponse getDepositTransactions(DepositTransactionsRequest request) {
        List<DepositTransactionItem> transactions = mockAccountMapper.selectDepositTransactions(request.getOrgCode(), request.getAccountNum(),
                request.getSeqno(), request.getFromDate(), request.getToDate(), request.getLimit(), 0);
        DepositTransactionsResponse response = success(new DepositTransactionsResponse());
        response.setTransCnt(transactions.size());
        response.setTransList(transactions);
        return response;
    }

    private int pageOffset(String nextPage) {
        if (nextPage == null || nextPage.isBlank()) return 0;
        try {
            int offset = Integer.parseInt(nextPage);
            if (offset < 0) throw new NumberFormatException();
            return offset;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("next_page must be a non-negative page offset");
        }
    }

    private <T> void setNextPageIfPresent(List<T> items, int limit, int offset, java.util.function.Consumer<String> setter) {
        if (items.size() > limit) {
            items.remove(items.size() - 1);
            setter.accept(String.valueOf(offset + limit));
        }
    }

    private <T extends Object> T success(T response) {
        if (response instanceof BankAccountsResponse value) { value.setRspCode("00000"); value.setRspMsg("SUCCESS"); }
        if (response instanceof DepositBasicResponse value) { value.setRspCode("00000"); value.setRspMsg("SUCCESS"); }
        if (response instanceof DepositDetailResponse value) { value.setRspCode("00000"); value.setRspMsg("SUCCESS"); }
        if (response instanceof DepositTransactionsResponse value) { value.setRspCode("00000"); value.setRspMsg("SUCCESS"); }
        return response;
    }
}
