package com.example.trading.transaction;

import com.example.trading.Application;
import com.example.trading.user.User;
import com.example.trading.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = Application.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@AutoConfigureRestDocs(outputDir = "target/snippets")
class TransactionResourceTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository txRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldReturnNotFound() throws Exception {
        this.mockMvc.perform(get("/users/1/transactions"))
            .andExpect(status().isNotFound());
    }

    private ResultActions postTx(User user, String json) throws Exception {
        return this.mockMvc.perform(post("/users/" + user.getId() + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json));
    }

    @Test
    public void testCreateTransaction() throws Exception {
        User bob = createTestUser("bob", "bob@bob.com");
        String json = "{\"btcAmount\":1}";
        postTx(bob, json)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("postTx"));

        this.mockMvc.perform(get("/users/" + bob.getId() + "/transactions"))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("getAllTx"));

        deleteTestUser(bob.getId());
    }

    @Test
    public void shouldReturnNoTransactions() throws Exception {
        User bob = createTestUser("bob", "bob@bob.com");
        this.mockMvc.perform(get("/users/" + bob.getId() + "/transactions"))
            .andExpect(status().isOk())
            .andExpect(content().string("[]"));
        deleteTestUser(bob.getId());
    }

    @Test
    public void shouldReturnInsufficientBtc() throws Exception {
        User bob = createTestUser("bob", "bob@bob.com");

        // specifying BTC to sell
        String json = new ObjectMapper().writeValueAsString(new Order(-1, 0));
        postTx(bob, json).andExpect(status().isBadRequest());

        // specifying USD to buy
        json = new ObjectMapper().writeValueAsString(new Order(0, 100));
        postTx(bob, json).andExpect(status().isBadRequest());

        deleteTestUser(bob.getId());
    }

    @Test
    public void shouldReturnInsufficientUsd() throws Exception {
        User bob = createTestUser("bob", "bob@bob.com");

        // specifying BTC to buy
        String json = new ObjectMapper().writeValueAsString(new Order(20, 0));
        postTx(bob, json).andExpect(status().isBadRequest());

        // specifying USD to sell
        json = new ObjectMapper().writeValueAsString(new Order(0, -2000));
        postTx(bob, json).andExpect(status().isBadRequest());

        deleteTestUser(bob.getId());
    }

    private User createTestUser(String name, String email) {
        User user = new User(name, email);
        return userRepository.saveAndFlush(user);
    }

    private void deleteTestUser(long id) {
        userRepository.deleteById(id);
        userRepository.flush();
    }
}
