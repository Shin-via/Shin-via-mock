package com.via.shinviamock.loan.service;


import com.via.shinviamock.loan.dto.mydata.LoanAccountItem;
import com.via.shinviamock.loan.dto.mydata.MyDataLoanResponses;
import com.via.shinviamock.loan.mapper.MockLoanAccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MockLoanAccountService {
    private final MockLoanAccountMapper mockLoanAccountMapper;


    public MyDataLoanResponses.LoanList getMyDataLoanAccounts(
            String ci,
            String orgCode, String searchTimeStamp, int limit, int offset

    ) {
        List<LoanAccountItem>  loans = mockLoanAccountMapper.selectMyDataLoanAccounts(ci, orgCode, limit+1, offset);

        boolean hasNext = loans.size() > limit;
        if (hasNext) {
            loans.remove(loans.size() -1);
        }

        MyDataLoanResponses.LoanList response =new MyDataLoanResponses.LoanList();

        response.setSearchTimestamp(searchTimeStamp);
        response.setNextPage(hasNext ? String.valueOf(offset+limit) : null);
        response.setLoanCnt(loans.size());
        response.setLoanList(loans);
        return response;
    }
}
