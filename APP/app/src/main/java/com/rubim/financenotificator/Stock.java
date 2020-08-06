package com.rubim.financenotificator;

public class Stock {

    private String stockExchange, objective, objectiveText, last_trade;
    private double expectedValue, value;

    public Stock(String stockExchange, String objective, String objectiveText, String last_trade, double expectedValue, double value) {
        this.stockExchange = stockExchange;
        this.objective = objective;
        this.objectiveText = objectiveText;
        this.last_trade = last_trade;
        this.expectedValue = expectedValue;
        this.value = value;
    }

    public String getStockExchange() {
        return stockExchange;
    }

    public String getObjective() {
        return objective;
    }

    public String getObjectiveText() {
        return objectiveText;
    }

    public String getLast_trade() {
        return last_trade;
    }

    public double getExpectedValue() {
        return expectedValue;
    }

    public double getValue() {
        return value;
    }
}
