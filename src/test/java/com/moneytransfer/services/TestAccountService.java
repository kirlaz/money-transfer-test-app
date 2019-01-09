package com.moneytransfer.services;

import com.moneytransfer.model.Account;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestAccountService extends TestService {

    @Test
    public void testGetAllAccounts() throws IOException, URISyntaxException {
        // Arrange
        URI uri = builder.setPath("/account/all").build();
        HttpGet request = new HttpGet(uri);
        // Act
        HttpResponse response = client.execute(request);
        // Assert
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode);
        //check the content
        String jsonString = EntityUtils.toString(response.getEntity());
        Account[] accounts = mapper.readValue(jsonString, Account[].class);
        assertTrue(accounts.length > 0);
    }
}
