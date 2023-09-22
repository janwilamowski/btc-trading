package com.example.trading.transaction;

import com.example.trading.user.User;
import com.example.trading.user.UserRepository;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TransactionService {

    private final static int BTC_SCALE = 8;

    @Autowired
    private TransactionRepository txRepo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PriceService priceService;

    boolean isOrderValid(Order order) {
        // only one of USD or BTC amount must be nonzero
        BigDecimal btcAmount = order.btcAmount().orElse(BigDecimal.ZERO);
        BigDecimal usdAmount = order.usdAmount().orElse(BigDecimal.ZERO);
        return btcAmount.compareTo(BigDecimal.ZERO) == 0 ^
                usdAmount.compareTo(BigDecimal.ZERO) == 0;
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Transaction createTransaction(User user, Order order) {
        BigDecimal btcPrice = priceService.getCurrentBtcPrice();
        if (! isOrderValid(order)) {
            throw new IllegalArgumentException("Exactly one of BTC or USD amount must be set");
        }
        Transaction tx = new Transaction(user, order, btcPrice);
        BigDecimal buyBtcAmount = null;
        BigDecimal buyUsdAmount = null;
        if (order.btcAmount().orElse(BigDecimal.ZERO).compareTo(BigDecimal.ZERO) < 0) {
            // selling BTC
            BigDecimal sellBtcAmount = order.btcAmount().get().abs();
            buyBtcAmount = sellBtcAmount.negate();
            if (user.getBalanceBtc().compareTo(sellBtcAmount) < 0) {
                throw new IllegalArgumentException("insufficient BTC");
            }
            buyUsdAmount = sellBtcAmount.multiply(btcPrice);
            tx.setBalanceBtc(user.getBalanceBtc().subtract(sellBtcAmount));
            tx.setBalanceUsd(user.getBalanceUsd().add(buyUsdAmount));

        } else if (order.btcAmount().orElse(BigDecimal.ZERO).compareTo(BigDecimal.ZERO) > 0) {
            // buying BTC
            buyBtcAmount = order.btcAmount().get();
            BigDecimal sellUsdAmount = buyBtcAmount.multiply(btcPrice);
            buyUsdAmount = sellUsdAmount.negate();
            if (user.getBalanceUsd().compareTo(sellUsdAmount) < 0) {
                throw new IllegalArgumentException("insufficient USD");
            }
            tx.setBalanceBtc(user.getBalanceBtc().add(buyBtcAmount));
            tx.setBalanceUsd(user.getBalanceUsd().subtract(sellUsdAmount));

        } else if (order.usdAmount().orElse(BigDecimal.ZERO).compareTo(BigDecimal.ZERO) < 0) {
            // selling USD
            BigDecimal sellUsdAmount = order.usdAmount().get().abs();
            buyUsdAmount = sellUsdAmount.negate();
            if (user.getBalanceUsd().compareTo(sellUsdAmount) < 0) {
                throw new IllegalArgumentException("insufficient USD");
            }
            buyBtcAmount = sellUsdAmount.divide(btcPrice, BTC_SCALE, RoundingMode.HALF_EVEN);
            tx.setBalanceBtc(user.getBalanceBtc().add(buyBtcAmount));
            tx.setBalanceUsd(user.getBalanceUsd().subtract(sellUsdAmount));

        } else if (order.usdAmount().orElse(BigDecimal.ZERO).compareTo(BigDecimal.ZERO) > 0) {
            // buying USD
            buyUsdAmount = order.usdAmount().get();
            BigDecimal sellBtcAmount = buyUsdAmount.divide(btcPrice, BTC_SCALE, RoundingMode.HALF_EVEN);
            buyBtcAmount = sellBtcAmount.negate();
            if (user.getBalanceBtc().compareTo(sellBtcAmount) < 0) {
                throw new IllegalArgumentException("insufficient BTC");
            }
            tx.setBalanceBtc(user.getBalanceBtc().subtract(sellBtcAmount));
            tx.setBalanceUsd(user.getBalanceUsd().add(buyUsdAmount));
        }
        user.setBalanceBtc(user.getBalanceBtc().add(buyBtcAmount));
        user.setBalanceUsd(user.getBalanceUsd().add(buyUsdAmount));
        txRepo.save(tx);
        user.getTransactions().add(tx);
        userRepository.save(user);
        return tx;
    }
}
