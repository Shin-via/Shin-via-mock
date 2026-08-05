package com.via.shinviamock.loan.product.service;

import com.via.shinviamock.loan.product.client.FinlifeLoanProductClient;
import com.via.shinviamock.loan.product.converter.LoanProductConverter;
import com.via.shinviamock.loan.product.dto.external.credit.CreditResponse;
import com.via.shinviamock.loan.product.dto.external.jeonse.JeonseResponse;
import com.via.shinviamock.loan.product.dto.external.mortgage.MortgageResponse;
import com.via.shinviamock.loan.product.dto.response.LoanProductResponses;
import com.via.shinviamock.loan.product.exception.FinlifeApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanProductSyncService {

    private final FinlifeLoanProductClient client;
    private final LoanProductConverter converter;
    private final LoanProductPersistenceService persistenceService;

    // 주택담보대출 상품 정보 전체 가져오기
    public LoanProductResponses.SyncResult mortgage() {
        int page = 1, pages = 0, products = 0, options = 0;

        while (true) {
            MortgageResponse response = client.fetchMortgage(page);
            MortgageResponse.Result result = require(response == null ? null : response.result());
            validate(result.errorCode(), result.errorMessage());

            var count = persistenceService.saveHousing(
                    converter.mortgage(result.baseList(), result.optionList())
            );

            pages++;
            products += count.productCount();
            options += count.optionCount();

            int maxPage = result.maxPageNo() == null ? page : result.maxPageNo();
            if (page >= maxPage) break;
            page++;
        }

        return new LoanProductResponses.SyncResult(
                "MORTGAGE", pages, products, options
        );
    }

    // 전세자금대출 상품 전체 가져오기
    public LoanProductResponses.SyncResult jeonse() {
        int page = 1, pages = 0, products = 0, options = 0;

        while (true) {
            JeonseResponse response = client.fetchJeonse(page);
            JeonseResponse.Result result = require(response == null ? null : response.result());
            validate(result.errorCode(), result.errorMessage());

            var count = persistenceService.saveHousing(
                    converter.jeonse(result.baseList(), result.optionList())
            );

            pages++;
            products += count.productCount();
            options += count.optionCount();

            int maxPage = result.maxPageNo() == null ? page : result.maxPageNo();
            if (page >= maxPage) break;
            page++;
        }

        return new LoanProductResponses.SyncResult(
                "JEONSE", pages, products, options
        );
    }


    // 신용대출 상품 정보 전체 가져오기
    public LoanProductResponses.SyncResult credit() {
        int page = 1, pages = 0, products = 0, options = 0;

        while (true) {
            CreditResponse response = client.fetchCredit(page);
            CreditResponse.Result result = require(response == null ? null : response.result());
            validate(result.errorCode(), result.errorMessage());

            var count = persistenceService.saveCredit(
                    converter.credit(result.baseList(), result.optionList())
            );

            pages++;
            products += count.productCount();
            options += count.optionCount();

            int maxPage = result.maxPageNo() == null ? page : result.maxPageNo(); // 마지막 페이지까지 가져오기,
            if (page >= maxPage) break;
            page++;
        }

        return new LoanProductResponses.SyncResult(
                "CREDIT", pages, products, options
        );
    }

    public List<LoanProductResponses.SyncResult> allFinlife() {
        return List.of(mortgage(), jeonse(), credit());
    }

    private <T> T require(T result) {
        if (result == null) {
            throw new IllegalStateException("Empty Finlife response");
        }
        return result;
    }

    private void validate(String code, String message) {
        if (!"000".equals(code)) {
            throw new FinlifeApiException(code, message);
        }
    }
}
