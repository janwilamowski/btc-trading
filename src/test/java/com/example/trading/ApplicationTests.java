package com.example.trading;

import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @RunWith(SpringRunner.class)
// @SpringBootTest
// @WebMvcTest(GreetingController.class)
@AutoConfigureRestDocs(outputDir = "target/snippets")
@Disabled
class ApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	public void shouldCreateSnippets() throws Exception {
		this.mockMvc.perform(get("/greet"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Hello, World")))
			.andDo(document("home"));
	}

	@Test
	public void shouldReturnDefaultMessage() throws Exception {
		this.mockMvc.perform(get("/greet"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Hello, World")));
	}

	@Test
	public void shouldReturnNameIfGiven() throws Exception {
		this.mockMvc.perform(get("/greet?name=Hamlet"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Hello, Hamlet")));
	}

	@Test
	public void shouldReturnEmptyName() throws Exception {
		this.mockMvc.perform(get("/greet?name="))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Hello, ")));
	}

}
