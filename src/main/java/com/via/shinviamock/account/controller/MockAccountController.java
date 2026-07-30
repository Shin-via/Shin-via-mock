package com.via.shinviamock.account.controller;

import com.via.shinviamock.account.dto.response.AccountBalanceResponse;
import com.via.shinviamock.account.dto.response.AccountTransactionResponse;
import com.via.shinviamock.account.service.MockAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class MockAccountController {

    private final MockAccountService mockAccountService;

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
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header must be in 'Bearer <token>' format");
        }
    }
}
