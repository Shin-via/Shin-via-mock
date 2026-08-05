package com.via.shinviamock.loan.product.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public final class LoanProductResponses {

    private LoanProductResponses() {}

    public record SyncResult(
            String loanType,
            int pageCount,
            int productCount,
            int optionCount
    ) {}


    public record ProductDetail(
            Map<String, Object> product,
            Map<String, Object> detail,
            List<Map<String, Object>> options
    ) {
    }

    public record ListEnvelope(
            @JsonProperty("rsp_code") String responseCode,
            @JsonProperty("rsp_message") String responseMessage,
            @JsonProperty("product_list") List<Map<String, Object>> productList
    ) {
        public static ListEnvelope success(List<Map<String, Object>> products) {
            return new ListEnvelope("00000", "SUCCESS", products);
        }
    }

    public record DetailEnvelope(
            @JsonProperty("rsp_code") String responseCode,
            @JsonProperty("rsp_message") String responseMessage,
            @JsonProperty("product") Map<String, Object> product
    ) {
        public static DetailEnvelope success(Map<String, Object> product) {
            return new DetailEnvelope("00000", "SUCCESS", product);
        }
    }
}
