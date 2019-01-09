--This script is used for unit test cases, DO NOT CHANGE!

DROP TABLE IF EXISTS Account;

CREATE TABLE Account (
  accountNum VARCHAR(20) PRIMARY KEY NOT NULL,
  balance DECIMAL(19, 2),
  currencyCode VARCHAR(3)
);

INSERT INTO Account (accountNum, balance, currencyCode) VALUES ('A1', 100.00, 'USD');
INSERT INTO Account (accountNum, balance, currencyCode) VALUES ('B1', 200.00, 'USD');
INSERT INTO Account (accountNum, balance, currencyCode) VALUES ('A2', 500.00, 'EUR');
INSERT INTO Account (accountNum, balance, currencyCode) VALUES ('B2', 500.00, 'EUR');
INSERT INTO Account (accountNum, balance, currencyCode) VALUES ('A3', 500.00, 'GBP');
INSERT INTO Account (accountNum, balance, currencyCode) VALUES ('B3', 500.00, 'GBP');
