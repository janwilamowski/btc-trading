package com.example.trading.transaction;

import jakarta.persistence.Table;
import com.example.trading.user.User;
import java.math.BigDecimal;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import java.util.Date;
import org.springframework.data.annotation.CreatedDate;


@Entity
@Table
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @CreatedDate
    private Date createdAt = new Date();

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    private BigDecimal btcPrice;

    private BigDecimal balanceBtc;

    private BigDecimal balanceUsd;

    private BigDecimal orderAmountBtc;

    private BigDecimal orderAmountUsd;

    public Transaction() {}

    public Transaction(User user, Order order, BigDecimal btcPrice) {
        this.user = user;
        orderAmountBtc = order.btcAmount().orElse(BigDecimal.ZERO);
        orderAmountUsd = order.usdAmount().orElse(BigDecimal.ZERO);
        this.btcPrice = btcPrice;
    }

    public Date getCreatedAt() { return createdAt; }

    public BigDecimal getBtcPrice() { return btcPrice; }

    public BigDecimal getBalanceBtc() { return balanceBtc; }

    public BigDecimal getBalanceUsd() { return balanceUsd; }

    public BigDecimal getOrderAmountBtc() { return orderAmountBtc; }

    public BigDecimal getOrderAmountUsd() { return orderAmountUsd; }

    void setBalanceBtc(BigDecimal balanceBtc) {
        this.balanceBtc = balanceBtc;
    }

    void setBalanceUsd(BigDecimal balanceUsd) {
        this.balanceUsd = balanceUsd;
    }
}
