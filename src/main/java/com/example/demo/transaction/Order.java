package com.example.trading.transaction;

import java.math.BigDecimal;
import java.util.Optional;

public record Order(Optional<BigDecimal> btcAmount, Optional<BigDecimal> usdAmount) {

    public Order(int btcAmount, int usdAmount) {
        this(Optional.of(new BigDecimal(btcAmount)), Optional.of(new BigDecimal(usdAmount)));
    }
}
