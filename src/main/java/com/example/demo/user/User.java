package com.example.trading.user;

import com.example.trading.transaction.Transaction;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "\"user\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private String emailAddress;

    private BigDecimal balanceUsd = new BigDecimal(1000);

    private BigDecimal balanceBtc = new BigDecimal(0);

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.EAGER)
    private List<Transaction> transactions = new ArrayList<>();

    public User() {}

    public User(String name, String emailAddress) {
        this.name = name;
        this.emailAddress = emailAddress;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public BigDecimal getBalanceBtc() {
        return balanceBtc;
    }

    public void setBalanceBtc(BigDecimal balanceBtc) {
        this.balanceBtc = balanceBtc;
    }

    public BigDecimal getBalanceUsd() {
        return balanceUsd;
    }

    public void setBalanceUsd(BigDecimal balanceUsd) {
        this.balanceUsd = balanceUsd;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
