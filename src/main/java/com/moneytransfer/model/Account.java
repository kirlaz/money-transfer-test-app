package com.moneytransfer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {

    private String accountNum;

    private String currencyCode;

    private BigDecimal balance;

    public Account() {
    }

    public Account(BigDecimal balance, String currencyCode) {
        this.balance = balance;
        this.currencyCode = currencyCode;
    }

    public Account(String accountId, BigDecimal balance, String currencyCode) {
        this.accountNum = accountId;
        this.balance = balance;
        this.currencyCode = currencyCode;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountNum == account.accountNum &&
                Objects.equals(currencyCode, account.currencyCode) &&
                Objects.equals(balance, account.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNum, currencyCode, balance);
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountNum=" + accountNum +
                ", currencyCode='" + currencyCode + '\'' +
                ", balance=" + balance +
                '}';
    }
}
