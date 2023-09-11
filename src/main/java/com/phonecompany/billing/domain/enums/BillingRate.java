package com.phonecompany.billing.domain.enums;

import java.math.BigDecimal;

public enum BillingRate {
    NORMAL_RATE(new BigDecimal("1.00")),
    REDUCED_RATE(new BigDecimal("0.50")),
    ADDITIONAL_RATE(new BigDecimal("0.20"));

    private final BigDecimal rate;

    BillingRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getRate() {
        return this.rate;
    }
}