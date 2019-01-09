package com.moneytransfer.dao;

import com.moneytransfer.exception.MoneyTransferException;
import com.moneytransfer.exception.RequestParamException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.MoneyTransferRequest;

import java.util.List;

public interface AccountDAO {

    void executeTransfer(MoneyTransferRequest transfer) throws RequestParamException, MoneyTransferException;

    Account getAccountById(String accountNum) throws MoneyTransferException;

    List<Account> getAllAccounts() throws MoneyTransferException;

}
