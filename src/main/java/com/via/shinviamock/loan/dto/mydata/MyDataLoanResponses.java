package com.via.shinviamock.loan.dto.mydata;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;


// json 응답 형태
//{
//        "rsp_code": "0000",
//        "rsp_msg": "정상",
//        "search_timestamp": "",
//        "next_page": null,
//        "loan_cnt": 1,
//        "loan_list": []
//        }
//

public final class MyDataLoanResponses {

    private MyDataLoanResponses() {
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonAutoDetect(
            fieldVisibility = JsonAutoDetect.Visibility.ANY,
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE
    )
    public static class LoanList {

        private String rspCode = "0000";

        private String rspMsg = "정상";

        private String searchTimestamp;

        private String nextPage;

        private int loanCnt;

        private List<LoanAccountItem> loanList;
    }
}