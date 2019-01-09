package com.moneytransfer.services;


import com.moneytransfer.model.MoneyTransferRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;


public class TestTransferService extends TestService {

    @Test
    public void testWhenSufficientBalanceThenOK() throws IOException, URISyntaxException {
        // Arrange
        URI uri = builder.setPath("/transfer").build();
        BigDecimal amount = new BigDecimal(10).setScale(2, RoundingMode.HALF_EVEN);
        MoneyTransferRequest transaction = new MoneyTransferRequest("EUR", amount, "A2", "B2");

        String jsonInString = mapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);

        // Act
        HttpResponse response = client.execute(request);

        // Assert
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);
    }

    @Test
    public void testWhenNotSufficientBalanceThenErrorResponse() throws IOException, URISyntaxException {
        // Arrange
        URI uri = builder.setPath("/transfer").build();
        BigDecimal amount = new BigDecimal(100000).setScale(4, RoundingMode.HALF_EVEN);
        MoneyTransferRequest transaction = new MoneyTransferRequest("EUR", amount, "A2", "B2");

        String jsonInString = mapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);

        // Act
        HttpResponse response = client.execute(request);

        // Assert
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(400, statusCode);
    }

    @Test
    public void testWhenIncorrectCurrencyCodeThenErrorResponse() throws IOException, URISyntaxException {
        // Arrange
        URI uri = builder.setPath("/transfer").build();
        BigDecimal amount = new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN);
        MoneyTransferRequest transaction = new MoneyTransferRequest("USD", amount, "A2", "B2");

        String jsonInString = mapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);

        // Act
        HttpResponse response = client.execute(request);

        // Assert
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(400, statusCode);
    }

}
