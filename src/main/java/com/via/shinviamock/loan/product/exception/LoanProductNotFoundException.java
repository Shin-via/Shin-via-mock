package com.via.shinviamock.loan.product.exception;

public class LoanProductNotFoundException extends RuntimeException {
    public LoanProductNotFoundException(Long id) {
        super("Loan product not found: " + id);
    }
}
