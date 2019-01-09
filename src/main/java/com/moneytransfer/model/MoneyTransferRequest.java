package com.moneytransfer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;

public class MoneyTransferRequest {

    @JsonProperty(required = true)
    private String currencyCode;

    @JsonProperty(required = true)
    private BigDecimal amount;

    @JsonProperty(required = true)
    private String fromAccountNum;

    @JsonProperty(required = true)
    private String targetAccountNum;

    public MoneyTransferRequest() {
    }

    public MoneyTransferRequest(String currencyCode, BigDecimal amount, String fromAccountId, String toAccountId) {
        this.currencyCode = currencyCode;
        this.amount = amount;
        this.fromAccountNum = fromAccountId;
        this.targetAccountNum = toAccountId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getFromAccountNum() {
        return fromAccountNum;
    }

    public String getTargetAccountNum() {
        return targetAccountNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoneyTransferRequest that = (MoneyTransferRequest) o;
        return Objects.equals(currencyCode, that.currencyCode) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(fromAccountNum, that.fromAccountNum) &&
                Objects.equals(targetAccountNum, that.targetAccountNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currencyCode, amount, fromAccountNum, targetAccountNum);
    }

    @Override
    public String toString() {
        return "MoneyTransferRequest{" +
                "currencyCode='" + currencyCode + '\'' +
                ", amount=" + amount +
                ", fromAccountNum='" + fromAccountNum + '\'' +
                ", targetAccountNum='" + targetAccountNum + '\'' +
                '}';
    }
}
