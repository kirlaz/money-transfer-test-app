# Money transfer Rest API

A simple Java RESTful API for money transfers between accounts including data model and the backing implementation.

Main implementation points:
1. To avoid unconsistency during concurrent balance updates, both accounts are blocked for the duration of the transaction.
   A pessimistic blocking is used at the database level: 'SELECT * FROM Account WHERE accountNum = ? FOR UPDATE'
2. To prevent deadlocks, accounts are blocked in order according to their numbers.
3. Operation atomicity was implemented based on database transactions.

### How to run
```sh
mvn exec:java
```
Application starts a jetty server on localhost port 8080 and H2 in memory database initialized with some sample account data.

Check: http://localhost:8080/account/all

### Available Services

| HTTP METHOD | PATH | USAGE |
| -----------| ------ | ------ |
| GET | /account/all | get all accounts | 
| POST | /transfer | perform money transfer between 2 accounts | 

#### Sample JSON for Transfer:
```sh
{  
   "currencyCode":"EUR",
   "amount":100000.00,
   "fromAccountNum":1,
   "toAccountNum":2
}
```

### Http Status
- 200 OK: The request has succeeded
- 400 Bad Request: The request could not be understood by the server 
- 404 Not Found: The requested resource cannot be found
- 500 Internal Server Error: The server encountered an unexpected condition
 
 git test 1