package models;

import java.time.LocalDate;
import java.util.Currency;

/**
 * Created by felipe on 19/12/17.
 */
public class Expense {
    LocalDate date;
    String description;
    int amount;
    Currency currency;

    public Expense(LocalDate date, String description, int amount, Currency currency) {
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.currency = currency;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "date=" + date +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", currency=" + currency +
                '}';
    }

}
