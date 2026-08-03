package com.via.shinviamock.account.controller;

import com.via.shinviamock.account.dto.response.AccountBalanceResponse;
import com.via.shinviamock.account.dto.response.AccountTransactionResponse;
import com.via.shinviamock.account.service.MockAccountService;
import com.via.shinviamock.account.dto.request.*;
import com.via.shinviamock.account.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class MockAccountController {

    private final MockAccountService mockAccountService;

    @GetMapping("/v2/bank/accounts")
    public BankAccountsResponse getMyDataAccounts(@RequestParam("org_code") String orgCode,
                                                   @RequestParam(value = "search_timestamp", required = false) String searchTimestamp,
                                                   @RequestParam(value = "next_page", required = false) String nextPage,
                                                   @RequestParam("limit") Integer limit) {
        BankAccountsRequest request = new BankAccountsRequest();
        request.setOrgCode(orgCode);
        request.setSearchTimestamp(searchTimestamp);
        request.setNextPage(nextPage);
        request.setLimit(limit);
        validatePageRequest(orgCode, limit);
        return mockAccountService.getMyDataAccounts(request);
    }

    @PostMapping("/v2/bank/accounts/deposit/basic")
    public DepositBasicResponse getDepositBasics(@RequestBody DepositBasicRequest request) {
        validateAccountRequest(request.getOrgCode(), request.getAccountNum(), request.getSearchTimestamp());
        return mockAccountService.getDepositBasics(request);
    }

    @PostMapping("/v2/bank/accounts/deposit/detail")
    public DepositDetailResponse getDepositDetails(@RequestBody DepositDetailRequest request) {
        validateAccountRequest(request.getOrgCode(), request.getAccountNum(), request.getSearchTimestamp());
        return mockAccountService.getDepositDetails(request);
    }

    @PostMapping("/v2/bank/accounts/deposit/transactions")
    public DepositTransactionsResponse getDepositTransactions(@RequestBody DepositTransactionsRequest request) {
        validatePageRequest(request.getOrgCode(), request.getLimit());
        if (isBlank(request.getAccountNum()) || isBlank(request.getFromDate()) || isBlank(request.getToDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "account_num, from_date and to_date are required");
        }
        return mockAccountService.getDepositTransactions(request);
    }

    @GetMapping("/v2.0/account/balance/fin_num")
    public AccountBalanceResponse getAccountBalance(@RequestHeader("Authorization") String authorization,
                                                     @RequestParam("bank_tran_id") String bankTranId,
                                                     @RequestParam("fintech_use_num") String fintechUseNum,
                                                     @RequestParam("bank_code_std") String bankCodeStd) {
        validateAuthorization(authorization);
        return mockAccountService.getAccountBalance(bankTranId, fintechUseNum, bankCodeStd);
    }

    @GetMapping("/v2.0/account/transaction_list/fin_num")
    public AccountTransactionResponse getAccountTransactions(@RequestHeader("Authorization") String authorization,
                                                              @RequestParam("bank_tran_id") String bankTranId,
                                                              @RequestParam("fintech_use_num") String fintechUseNum,
                                                              @RequestParam("inquiry_type") String inquiryType,
                                                              @RequestParam("inquiry_base") String inquiryBase,
                                                              @RequestParam("from_date") String fromDate,
                                                              @RequestParam(value = "from_time", required = false) String fromTime,
                                                              @RequestParam("to_date") String toDate,
                                                              @RequestParam(value = "to_time", required = false) String toTime,
                                                              @RequestParam("sort_order") String sortOrder,
                                                              @RequestParam("tran_dtime") String tranDtime,
                                                              @RequestParam(value = "befor_inquiry_trace_info", required = false) String beforInquiryTraceInfo) {
        validateAuthorization(authorization);
        return mockAccountService.getAccountTransactions(bankTranId, fintechUseNum, inquiryType, inquiryBase,
                fromDate, fromTime, toDate, toTime, sortOrder, tranDtime, beforInquiryTraceInfo);
    }

    private void validateAuthorization(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization 헤더가 누락되었습니다.");
        }
    }

    private void validatePageRequest(String orgCode, Integer limit) {
        if (isBlank(orgCode) || limit == null || limit < 1 || limit > 500) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "org_code and limit (1..500) are required");
        }
    }

    private void validateAccountRequest(String orgCode, String accountNum, String searchTimestamp) {
        if (isBlank(orgCode) || isBlank(accountNum) || isBlank(searchTimestamp)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "org_code, account_num and search_timestamp are required");
        }
    }

    private boolean isBlank(String value) { return value == null || value.isBlank(); }
}
