package com.example.trading.transaction;

import java.math.BigDecimal;
import java.time.LocalTime;
import org.springframework.stereotype.Service;

@Service
public class PriceService {

    final int CYCLE_LENGTH_MINUTES = 6;
    final int CYCLE_LENGTH_MINUTES_HALF = CYCLE_LENGTH_MINUTES / 2;
    final int CYCLE_STEP_SECONDS = 5;
    final int STEPS_PER_MINUTE = 60 / CYCLE_STEP_SECONDS;
    final int CYCLE_MIN_VALUE = 100;
    final int CYCLE_MAX_VALUE = 460;
    final int STEP_SIZE = 10;

    /**
     * Calculate and return the current BTC price based on time.
     *
     * Since the price development is perfectly predictable, we don't
     * keep track of it but compute it on the fly. Every six minutes,
     * starting with minute zero of each hour, the cycle starts with
     * the base price (CYCLE_MIN_VALUE) and three minutes later, it
     * reaches the CYCLE_MAX_VALUE. Everything in between is calculated
     * based on how many steps have passed.
     */
    public BigDecimal getCurrentBtcPrice() {
        var now = LocalTime.now();
        var offsetMinutes = now.getMinute() % CYCLE_LENGTH_MINUTES;
        int currentStep = now.getSecond() / CYCLE_STEP_SECONDS;
        if (offsetMinutes < CYCLE_LENGTH_MINUTES_HALF) {
            // first half: rising phase
            int steps = offsetMinutes * STEPS_PER_MINUTE + currentStep;
            int price = CYCLE_MIN_VALUE + steps * STEP_SIZE;
            return new BigDecimal(price);
        }
        // second half: falling phase
        offsetMinutes -= CYCLE_LENGTH_MINUTES_HALF;
        int steps = offsetMinutes * STEPS_PER_MINUTE + currentStep;
        int price = CYCLE_MAX_VALUE - steps * STEP_SIZE;
        return new BigDecimal(price);
    }
}
