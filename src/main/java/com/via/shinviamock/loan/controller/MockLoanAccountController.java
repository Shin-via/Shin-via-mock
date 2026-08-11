package com.via.shinviamock.loan.controller;

import com.via.shinviamock.connection.service.MockTokenRedisService;
import com.via.shinviamock.loan.dto.mydata.MyDataLoanResponses;
import com.via.shinviamock.loan.service.MockLoanAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class MockLoanAccountController {
    private final MockLoanAccountService mockLoanAccountService;
    private final MockTokenRedisService mockTokenRedisService;


    // 사용자가 보유한 대출계좌 목록 조회

    @GetMapping("/v2/loan/accounts")
    public MyDataLoanResponses.LoanList getMyDataLoanAccounts (
            @RequestHeader(
                    value= "Authorization", required = false
            )String authorization,

            @RequestParam("org_code")
            String orgCode,

            @RequestParam(value = "search_timestamp", required= false, defaultValue="")
            String searchTimestamp,

            @RequestParam(value ="next_page", required = false)
            String nextPage,

            @RequestParam("limit")
            int limit


    )  {

        String ci = resolveCi(authorization);

        int validatedLimit = validateLimit(limit);
        int offset = parseNextPage(nextPage);

        return mockLoanAccountService.getMyDataLoanAccounts(
                ci, orgCode,searchTimestamp,validatedLimit, offset
        );




    }

    // Bearer Access Token -> Ci

    private String resolveCi(String authorization) {
        validateAuthorization(authorization);

        String cleanToken = authorization.startsWith("Bearer ") ? authorization.substring(7).trim() : authorization.trim();

        String ci = mockTokenRedisService.getCiByAccessToken(cleanToken);

        if (ci != null  && !ci.isBlank())  return ci;

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않거나 만료된 access token입니다.");


    }


    private void validateAuthorization (String auth) {
        if (auth == null || auth.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header 누락");
        }
    }
    private int validateLimit(int limit) {
        if (limit <1 || limit >500) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "limit은 1이상 500 이하");
        }

        return limit;
    }

    private int parseNextPage(String nextPage) {
        if (nextPage ==null ||  nextPage.isBlank()) {
            return 0;
        }

        try {
            int offset = Integer.parseInt(nextPage);
            if (offset<0) {
                throw new NumberFormatException();
            }
            return offset;
        }catch(NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST," next_page 형식이 올바르지 않음");
        }
    }





}
