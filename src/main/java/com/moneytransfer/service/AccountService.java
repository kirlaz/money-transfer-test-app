package com.moneytransfer.service;

import com.moneytransfer.dao.DAOFactory;
import com.moneytransfer.exception.MoneyTransferException;
import com.moneytransfer.model.Account;
import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Account Service
 */
@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountService {

    private final DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);

    private static Logger log = Logger.getLogger(AccountService.class);

    /**
     * Get all accounts
     * @return the list of accounts
     * @throws MoneyTransferException in case of error
     */
    @GET
    @Path("/all")
    public List<Account> getAllAccounts() throws MoneyTransferException {
        return daoFactory.getAccountDAO().getAllAccounts();
    }
}
