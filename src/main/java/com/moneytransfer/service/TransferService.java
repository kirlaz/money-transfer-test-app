package com.moneytransfer.service;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.moneytransfer.dao.DAOFactory;
import com.moneytransfer.exception.MoneyTransferException;
import com.moneytransfer.exception.RequestParamException;
import com.moneytransfer.model.MoneyTransferRequest;

@Path("/transfer")
@Produces(MediaType.APPLICATION_JSON)
public class TransferService {

	private final DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);
	
	/**
	 * Money transfer between two accounts
	 * @param transfer the money transfer request
	 * @return Response
	 */
	@POST
	public Response send(MoneyTransferRequest transfer)	{

		String currency = transfer.getCurrencyCode();
		if (ValidationUtils.validateCcyCode(currency)) {
			try {
				daoFactory.getAccountDAO().executeTransfer(transfer);
			} catch (MoneyTransferException e) {
				throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
			} catch (RequestParamException e) {
				throw new WebApplicationException(e.getMessage(), Response.Status.BAD_REQUEST);
			}
			return Response.status(Response.Status.OK).build();
		} else {
			throw new WebApplicationException("Currency Code Invalid ", Response.Status.BAD_REQUEST);
		}

	}

}
