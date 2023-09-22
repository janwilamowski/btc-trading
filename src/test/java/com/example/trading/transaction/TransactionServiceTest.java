package com.example.trading.transaction;

import com.example.trading.Application;
import com.example.trading.user.User;
import com.example.trading.user.UserRepository;
import java.math.BigDecimal;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest(classes = Application.class)
class TransactionServiceTests {

    private static final BigDecimal BTC_PRICE = new BigDecimal(100);

    @Autowired
    private TransactionService txService;

    @MockBean
    private PriceService priceService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TransactionRepository txRepo;

    @Test
    public void testInvalidOrder() {
        assertThat(txService.isOrderValid(new Order(1, 1))).isFalse();
        assertThat(txService.isOrderValid(new Order(-1, -1))).isFalse();
        assertThat(txService.isOrderValid(new Order(0, 0))).isFalse();
    }

    @Test
    public void testValidOrder() {
        assertThat(txService.isOrderValid(new Order(1, 0))).isTrue();
        assertThat(txService.isOrderValid(new Order(0, 1))).isTrue();
        assertThat(txService.isOrderValid(new Order(-1, 0))).isTrue();
        assertThat(txService.isOrderValid(new Order(0, -1))).isTrue();
    }

    @Test
    public void testCannotCreateTxFromInvalidOrder() {
        User user = new User();
        assertThatThrownBy( () -> {
            txService.createTransaction(user, new Order(0, 0));
        }).isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Exactly one of BTC or USD amount must be set");
    }

    @Test
    public void testCannotCreateTxFromInsufficientUsd() {
        Mockito.when(priceService.getCurrentBtcPrice()).thenReturn(BTC_PRICE);
        User user = new User();
        assertThatThrownBy( () -> {
            txService.createTransaction(user, new Order(100, 0));
        }).isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("insufficient USD");

        assertThatThrownBy( () -> {
            txService.createTransaction(user, new Order(0, -2000));
        }).isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("insufficient USD");
    }

    @Test
    public void testCannotCreateTxFromInsufficientBtc() {
        Mockito.when(priceService.getCurrentBtcPrice()).thenReturn(BTC_PRICE);
        User user = new User();
        assertThatThrownBy( () -> {
            txService.createTransaction(user, new Order(0, 100));
        }).isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("insufficient BTC");

        assertThatThrownBy( () -> {
            txService.createTransaction(user, new Order(-1, 0));
        }).isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("insufficient BTC");
    }

    @Test
    public void testBuyBtc() {
        User user = new User();
        Mockito.when(priceService.getCurrentBtcPrice()).thenReturn(BTC_PRICE);
        Transaction result = txService.createTransaction(user, new Order(1, 0));
        assertThat(result.getBtcPrice()).isEqualTo(BTC_PRICE);
        assertThat(result.getOrderAmountBtc().intValue()).isEqualTo(1);
        assertThat(result.getOrderAmountUsd().intValue()).isEqualTo(0);
        assertThat(result.getBalanceBtc().intValue()).isEqualTo(1);
        assertThat(result.getBalanceUsd().intValue()).isEqualTo(900);
    }

    @Test
    public void testSellUsd() {
        User user = new User();
        Mockito.when(priceService.getCurrentBtcPrice()).thenReturn(BTC_PRICE);
        Transaction result = txService.createTransaction(user, new Order(0, -100));
        assertThat(result.getBtcPrice()).isEqualTo(BTC_PRICE);
        assertThat(result.getOrderAmountBtc().intValue()).isEqualTo(0);
        assertThat(result.getOrderAmountUsd().intValue()).isEqualTo(-100);
        assertThat(result.getBalanceBtc().intValue()).isEqualTo(1);
        assertThat(result.getBalanceUsd().intValue()).isEqualTo(900);
    }
}
