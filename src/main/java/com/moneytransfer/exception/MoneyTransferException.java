package com.moneytransfer.exception;

public class MoneyTransferException extends Exception {

    private static final long serialVersionUID = 2L;

    public MoneyTransferException(String msg) {
        super(msg);
    }

    public MoneyTransferException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
