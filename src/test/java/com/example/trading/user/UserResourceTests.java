package com.example.trading.user;

import com.example.trading.Application;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
class UserResourceTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldReturnNoUsers() throws Exception {
        this.mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(content().string("[]"));
    }

    @Test
    public void createUser() throws Exception {
        User bob = new User("bob", "bob@bob.com");
        String json = new ObjectMapper().writeValueAsString(bob);
        MvcResult result = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(json))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andDo(document("postUser"))
            .andReturn();

        String location = result.getResponse().getHeader("Location");
        assertThat(location).matches(".+/users/\\d+");

        List<User> found = userRepository.findAll();
        assertThat(found).extracting(User::getName).containsOnly("bob");
        deleteTestUser(1L);

        this.mockMvc.perform(get("/users/2"))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("getUser"));

        this.mockMvc.perform(get("/users"))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("getAllUsers"));
    }

    @Test
    public void deleteUserSuccess() throws Exception {
        User user = createTestUser("bob", "bob@bob.com");
        mockMvc.perform(delete("/users/" + user.getId()))
            .andDo(print())
            .andExpect(status().isNoContent())
            .andDo(document("deleteUser"));
        deleteTestUser(user.getId());
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
