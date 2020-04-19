CREATE TABLE FinanceAppConfig(
	id INTEGER UNIQUE NOT NULL AUTO_INCREMENT,
	apiKey VARCHAR(255),
	stockExchange VARCHAR(255),
	expectedPrice FLOAT,
	CONSTRAINT PK_FinanceAppConfig PRIMARY KEY(id)
);

INSERT INTO FinanceAppConfig (id, apiKey, stockExchange, expectedPrice) VALUES (1, 'YOUR_API_KEY', 'MSFT', 180.25);