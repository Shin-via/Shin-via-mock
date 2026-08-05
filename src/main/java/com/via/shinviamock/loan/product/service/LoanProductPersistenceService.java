package com.via.shinviamock.loan.product.service;

import com.via.shinviamock.loan.product.dto.command.LoanProductModels;
import com.via.shinviamock.loan.product.mapper.LoanProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanProductPersistenceService {

    private final LoanProductMapper mapper;

    @Transactional
    public SaveCount saveHousing(List<LoanProductModels.HousingProduct> products) {
        int productCount = 0;
        int optionCount = 0;

        for (LoanProductModels.HousingProduct normalized : products) {
            mapper.upsertProduct(normalized.product());

            Long productId = mapper.findProductId(
                    normalized.product().sourceType(),
                    normalized.product().sourceProductKey()
            );

            if (productId == null) {
                throw new IllegalStateException("Failed to resolve loan_product_id");
            }

            mapper.upsertHousingDetail(productId, normalized.detail());
            mapper.deleteHousingOptions(productId);

            for (LoanProductModels.HousingOption option : normalized.options()) {
                mapper.insertHousingOption(productId, option);
                optionCount++;
            }
            productCount++;
        }

        return new SaveCount(productCount, optionCount);
    }

    @Transactional
    public SaveCount saveCredit(List<LoanProductModels.CreditProduct> products) {
        int productCount = 0;
        int optionCount = 0;

        for (LoanProductModels.CreditProduct normalized : products) {
            mapper.upsertProduct(normalized.product());

            Long productId = mapper.findProductId(
                    normalized.product().sourceType(),
                    normalized.product().sourceProductKey()
            );

            if (productId == null) {
                throw new IllegalStateException("Failed to resolve loan_product_id");
            }

            mapper.upsertCreditDetail(productId, normalized.detail());
            mapper.deleteCreditOptions(productId);

            for (LoanProductModels.CreditOption option : normalized.options()) {
                mapper.insertCreditOption(productId, option);
                optionCount++;
            }
            productCount++;
        }

        return new SaveCount(productCount, optionCount);
    }

    public record SaveCount(int productCount, int optionCount) {}
}
