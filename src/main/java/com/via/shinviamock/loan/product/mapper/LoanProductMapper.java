package com.via.shinviamock.loan.product.mapper;

import com.via.shinviamock.loan.product.dto.command.LoanProductModels;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface LoanProductMapper {

    int upsertProduct(LoanProductModels.Product product);

    Long findProductId(
            @Param("sourceType") String sourceType,
            @Param("sourceProductKey") String sourceProductKey
    );

    int upsertHousingDetail(
            @Param("loanProductId") Long loanProductId,
            @Param("detail") LoanProductModels.HousingDetail detail
    );

    int deleteHousingOptions(@Param("loanProductId") Long loanProductId);

    int insertHousingOption(
            @Param("loanProductId") Long loanProductId,
            @Param("option") LoanProductModels.HousingOption option
    );

    int upsertCreditDetail(
            @Param("loanProductId") Long loanProductId,
            @Param("detail") LoanProductModels.CreditDetail detail
    );

    int deleteCreditOptions(@Param("loanProductId") Long loanProductId);

    int insertCreditOption(
            @Param("loanProductId") Long loanProductId,
            @Param("option") LoanProductModels.CreditOption option
    );

    List<Map<String, Object>> findProductSummaries(
            @Param("loanType") String loanType,
            @Param("active") Boolean active
    );

    Map<String, Object> findProductSummaryById(
            @Param("loanProductId") Long loanProductId
    );

    Map<String, Object> findHousingDetail(
            @Param("loanProductId") Long loanProductId
    );

    List<Map<String, Object>> findHousingOptions(
            @Param("loanProductId") Long loanProductId
    );

    Map<String, Object> findCreditDetail(
            @Param("loanProductId") Long loanProductId
    );

    List<Map<String, Object>> findCreditOptions(
            @Param("loanProductId") Long loanProductId
    );

    Map<String, Object> findStudentDetail(
            @Param("loanProductId") Long loanProductId
    );

    

}
