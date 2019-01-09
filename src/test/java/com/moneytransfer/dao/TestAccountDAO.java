package com.moneytransfer.dao;

import com.moneytransfer.exception.MoneyTransferException;
import com.moneytransfer.exception.RequestParamException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.MoneyTransferRequest;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.CountDownLatch;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

public class TestAccountDAO {

    private static final DAOFactory h2DaoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);

    private static Logger log = Logger.getLogger(TestAccountDAO.class);

    @BeforeClass
    public static void setup() {
        h2DaoFactory.populateTestData();
    }

    @Test
    public void testGetAccountById() throws MoneyTransferException {
        // Act
        Account account = h2DaoFactory.getAccountDAO().getAccountById("A1");
        // Assert
        assertEquals("A1", account.getAccountNum());
    }

    @Test
    public void testGetNonExistingAccById() throws MoneyTransferException {
        // Act
        Account account = h2DaoFactory.getAccountDAO().getAccountById("AA");
        // Assert
        assertNull(account);
    }

    @Test
    public void testSingleThreadSameCcyTransfer() throws MoneyTransferException, RequestParamException {
        // Arrange
        final AccountDAO accountDAO = h2DaoFactory.getAccountDAO();
        BigDecimal transferAmount = new BigDecimal(50.01).setScale(2, RoundingMode.HALF_EVEN);
        MoneyTransferRequest transfer = new MoneyTransferRequest("EUR", transferAmount, "A2", "B2");

        // Act
        long startTime = System.currentTimeMillis();
        accountDAO.executeTransfer(transfer);
        long endTime = System.currentTimeMillis();
        log.info("TransferAccountBalance finished, time taken: " + (endTime - startTime) + "ms");

        // Assert
        Account accountFrom = accountDAO.getAccountById("A2");
        log.debug("Account From: " + accountFrom);

        Account accountTo = accountDAO.getAccountById("B2");
        log.debug("Account From: " + accountTo);

        assertEquals(new BigDecimal(449.99).setScale(2, RoundingMode.HALF_EVEN), accountFrom.getBalance());
        assertEquals(new BigDecimal(550.01).setScale(2, RoundingMode.HALF_EVEN), accountTo.getBalance());
    }

    @Test(expected = RequestParamException.class)
    public void testSameAccountTransfer() throws MoneyTransferException, RequestParamException {
        // Arrange
        final AccountDAO accountDAO = h2DaoFactory.getAccountDAO();
        Account before = h2DaoFactory.getAccountDAO().getAccountById("A2");

        BigDecimal transferAmount = new BigDecimal(50.01).setScale(2, RoundingMode.HALF_EVEN);
        MoneyTransferRequest transfer = new MoneyTransferRequest("EUR", transferAmount, "A2", "A2");

        // Act
        accountDAO.executeTransfer(transfer);
    }

    @Test
    public void testAccountMultiThreadedTransfer() throws InterruptedException, MoneyTransferException {
        // Arrange
        final AccountDAO accountDAO = h2DaoFactory.getAccountDAO();
        final int THREADS_COUNT = 100;
        final CountDownLatch latch = new CountDownLatch(THREADS_COUNT);

        // Act
        for (int i = 0; i < THREADS_COUNT; i++) {
            new Thread(() -> {
                try {
                    MoneyTransferRequest transaction = new MoneyTransferRequest("USD",
                            new BigDecimal(2).setScale(2, RoundingMode.HALF_EVEN), "A1", "B1");
                    accountDAO.executeTransfer(transaction);
                } catch (Exception e) {
                    log.error("Error occurred during transfer ", e);
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        latch.await();

        // Assert
        Account accountFrom = accountDAO.getAccountById("A1");
        Account accountTo = accountDAO.getAccountById("B1");

        assertEquals(new BigDecimal(0).setScale(2, RoundingMode.HALF_EVEN), accountFrom.getBalance());
        assertEquals(new BigDecimal(300).setScale(2, RoundingMode.HALF_EVEN), accountTo.getBalance());
    }

    @Test
    public void testTransferFailOnDBLock() throws MoneyTransferException {
        // Arrange
        final String SQL_LOCK_ACC = "SELECT * FROM Account WHERE accountNum = 'A3' FOR UPDATE";

        BigDecimal A3Balance = h2DaoFactory.getAccountDAO().getAccountById("A3").getBalance();
        BigDecimal B3Balance = h2DaoFactory.getAccountDAO().getAccountById("B3").getBalance();

        // Act
        try (Connection conn = H2DAOFactory.getConnection();
             PreparedStatement lockStmt = conn.prepareStatement(SQL_LOCK_ACC)
        ) {
            conn.setAutoCommit(false);

            // lock account for writing:
            ResultSet rs = lockStmt.executeQuery();
            if (!rs.next()) {
                throw new RequestParamException("Locking error during test, SQL = " + SQL_LOCK_ACC);
            }

            // after lock account A3, try to transfer from account B3 to A3
            // default h2 timeout for acquire lock is 1sec
            BigDecimal transferAmount = new BigDecimal(50).setScale(2, RoundingMode.HALF_EVEN);
            MoneyTransferRequest transfer = new MoneyTransferRequest("GBP", transferAmount, "B3", "A3");
            h2DaoFactory.getAccountDAO().executeTransfer(transfer);

            conn.commit();
        } catch (Exception e1) {
            log.error("Exception occurred: ", e1);
        }

        // Assert
        assertEquals(B3Balance, h2DaoFactory.getAccountDAO().getAccountById("B3").getBalance());
        assertEquals(A3Balance, h2DaoFactory.getAccountDAO().getAccountById("A3").getBalance());
    }

}