package com.moneytransfer.dao.impl;

import com.moneytransfer.dao.H2DAOFactory;
import com.moneytransfer.dao.AccountDAO;
import com.moneytransfer.exception.MoneyTransferException;
import com.moneytransfer.exception.RequestParamException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.MoneyTransferRequest;
import com.moneytransfer.service.ValidationUtils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import javax.ws.rs.BadRequestException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountDAOImpl implements AccountDAO {

    private static Logger log = Logger.getLogger(AccountDAOImpl.class);

    private final static String SQL_GET_ACC_BY_ID = "SELECT * FROM Account WHERE accountNum = ?";
    private final static String SQL_LOCK_ACC_BY_ID = "SELECT * FROM Account WHERE accountNum = ? FOR UPDATE";
    private final static String SQL_UPDATE_ACC_BALANCE = "UPDATE Account SET balance = ? WHERE accountNum = ?";
    private final static String SQL_GET_ALL_ACC = "SELECT * FROM Account";

    @Override
    public void executeTransfer(MoneyTransferRequest transfer) throws RequestParamException, MoneyTransferException {
        try (Connection conn = H2DAOFactory.getConnection()) {
            conn.setAutoCommit(false);

            try {
                Account from, to;

                // ordering to exclude deadlocks
                if (String.CASE_INSENSITIVE_ORDER.compare(transfer.getFromAccountNum(), transfer.getTargetAccountNum()) == 0) {
                    throw new RequestParamException("Fail to transfer money within one account");
                }
                else if (String.CASE_INSENSITIVE_ORDER.compare(transfer.getFromAccountNum(), transfer.getTargetAccountNum()) > 0) {
                    from = lockAccount(conn, transfer.getFromAccountNum());
                    to = lockAccount(conn, transfer.getTargetAccountNum());
                }
                else    {
                    to = lockAccount(conn, transfer.getTargetAccountNum());
                    from = lockAccount(conn, transfer.getFromAccountNum());
                }

                checkTransfer(from, to, transfer);

                executeTransfer(conn, from, to, transfer);

            } catch (MoneyTransferException | RequestParamException e) {
                try {
                    conn.rollback();
                } catch (SQLException re) {
                    log.error("Fail to rollback transaction", re);
                }

                throw e;
            }

            conn.commit();
        } catch (SQLException e) {
            throw new MoneyTransferException("Fail to get connection", e);
        }
    }

    @Override
    public Account getAccountById(String accountNum) throws MoneyTransferException {
        Account acc = null;
        try (
                Connection conn = H2DAOFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_GET_ACC_BY_ID);
        ) {
            stmt.setString(1, accountNum);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                acc = resultSetToAccount(rs);;
                log.debug("Retrieve Account By Id: " + acc);
            }
            return acc;
        } catch (SQLException e) {
            throw new MoneyTransferException("getAccountById(): Error reading account data", e);
        }
    }

    /**
     * Get all accounts.
     */
    public List<Account> getAllAccounts() throws MoneyTransferException {
        List<Account> allAccounts = new ArrayList<Account>();
        try (
                Connection conn = H2DAOFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_GET_ALL_ACC);
        ) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Account acc = resultSetToAccount(rs);
                log.debug("getAllAccounts(): Get  Account " + acc);
                allAccounts.add(acc);
            }
            return allAccounts;
        } catch (SQLException e) {
            throw new MoneyTransferException("getAccountById(): Error reading account data", e);
        }
    }

    private Account resultSetToAccount(ResultSet rs) throws SQLException {
        return new Account(rs.getString("accountNum"),
                rs.getBigDecimal("balance"),
                rs.getString("currencyCode"));
    }

    private Account lockAccount(Connection conn, String accountNum) throws MoneyTransferException, RequestParamException {
        try (PreparedStatement lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID)) {
            lockStmt.setString(1, accountNum);
            ResultSet rs = lockStmt.executeQuery();
            if (rs.next()) {
                Account account = resultSetToAccount(rs);;
                log.debug("Lock account: " + account);
                return account;
            }
        } catch (SQLException e) {
            log.error("Fail to lock account: " + e);
            throw new MoneyTransferException("Fail to lock account: " + accountNum);
        }

        throw new RequestParamException("Fail to lock account: " + accountNum);
    }

    private void executeTransfer(Connection conn, Account from, Account to, MoneyTransferRequest transfer) throws
            MoneyTransferException {

        try (PreparedStatement updateStmt = conn.prepareStatement(SQL_UPDATE_ACC_BALANCE)) {
            updateStmt.setBigDecimal(1, from.getBalance().subtract(transfer.getAmount()));
            updateStmt.setString(2, from.getAccountNum());
            updateStmt.addBatch();

            updateStmt.setBigDecimal(1, to.getBalance().add(transfer.getAmount()));
            updateStmt.setString(2, to.getAccountNum());
            updateStmt.addBatch();

            int[] rowsUpdated = updateStmt.executeBatch();
            int result = rowsUpdated[0] + rowsUpdated[1];

            log.debug("Number of rows updated for the transfer : " + result);

            if (result != 2) {
                log.error("Not expected update statement results: " + result);
                throw new MoneyTransferException("Fail to transfer money, " +
                        "internal error (not expected update statement results)");
            }

        } catch (SQLException e) {
            log.error("Fail to update accounts balance: " + e);
            throw new MoneyTransferException("Fail to transfer money, " +
                    "internal error (fail to update accounts balance)");
        }
    }

    private void checkTransfer(Account from, Account to, MoneyTransferRequest transfer) throws RequestParamException {
        // check transaction currency
        if (!from.getCurrencyCode().equals(transfer.getCurrencyCode())) {
            throw new RequestParamException(
                    "Fail to transfer money, transfer currency is different from source/destination");
        }

        // check ccy is the same for both accounts
        if (!from.getCurrencyCode().equals(to.getCurrencyCode())) {
            throw new RequestParamException(
                    "Fail to transfer money, the source and target account are in different currency");
        }

        // check enough fund in source account
        BigDecimal fromAccountNewBalance = from.getBalance().subtract(transfer.getAmount());
        if (fromAccountNewBalance.compareTo(ValidationUtils.zero) < 0) {
            throw new RequestParamException("Not enough Balance from source Account ");
        }
    }
}
