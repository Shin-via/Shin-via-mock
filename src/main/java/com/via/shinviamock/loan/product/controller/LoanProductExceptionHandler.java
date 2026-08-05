package com.via.shinviamock.loan.product.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.via.shinviamock.loan.product.exception.FinlifeApiException;
import com.via.shinviamock.loan.product.exception.LoanProductNotFoundException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice(basePackages = "com.via.shinviamock.loan.product")
public class LoanProductExceptionHandler {

    @ExceptionHandler(LoanProductNotFoundException.class)
    public ResponseEntity<ErrorBody> notFound(LoanProductNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorBody("40400", e.getMessage()));
    }

    @ExceptionHandler(FinlifeApiException.class)
    public ResponseEntity<ErrorBody> finlife(FinlifeApiException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorBody(e.getErrorCode(), e.getMessage()));
    }

    public record ErrorBody(
            @JsonProperty("rsp_code") String code,
            @JsonProperty("rsp_message") String message
    ) {}
}
