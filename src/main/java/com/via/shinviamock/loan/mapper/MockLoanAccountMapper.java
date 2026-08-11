package com.via.shinviamock.loan.mapper;


import com.via.shinviamock.loan.dto.mydata.LoanAccountItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MockLoanAccountMapper {

    List<LoanAccountItem> selectMyDataLoanAccounts(
            @Param("ci") String ci,
            @Param("orgCode") String orgCode,
            @Param("limit") int limit,
            @Param("offset") int offset
    );
}
