package lk.student.api.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class StudentApiApplicationTests {

	private static final String BODY = "{\"name\":\"Kamal\",\"email\":\"%s\",\"course\":\"EEI6369\"}";

	@Autowired
	WebApplicationContext context;

	@Test
	void crudLifecycle() throws Exception {
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();

		String created = mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
				.content(BODY.formatted("kamal@example.com")))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andReturn().getResponse().getContentAsString();
		Number id = JsonPath.read(created, "$.id");

		mvc.perform(get("/api/students")).andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.email == 'kamal@example.com')]").exists());

		mvc.perform(put("/api/students/" + id).contentType(MediaType.APPLICATION_JSON)
				.content(BODY.formatted("kamal2@example.com")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("kamal2@example.com"));

		mvc.perform(delete("/api/students/" + id)).andExpect(status().isNoContent());
		mvc.perform(get("/api/students/" + id)).andExpect(status().isNotFound());
	}

	@Test
	void invalidBodyReturns400() throws Exception {
		MockMvcBuilders.webAppContextSetup(context).build()
				.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"\",\"email\":\"bad\",\"course\":\"\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void duplicateEmailReturns409() throws Exception {
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
		mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
				.content(BODY.formatted("dup@example.com")))
				.andExpect(status().isCreated());
		mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
				.content(BODY.formatted("dup@example.com")))
				.andExpect(status().isConflict());
	}

	@Test
	void getByIdReturnsStudent() throws Exception {
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
		String created = mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
				.content(BODY.formatted("byid@example.com")))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
		Number id = JsonPath.read(created, "$.id");
		mvc.perform(get("/api/students/" + id)).andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("byid@example.com"));
	}

	@Test
	void updateMissingStudentReturns404() throws Exception {
		MockMvcBuilders.webAppContextSetup(context).build()
				.perform(put("/api/students/999999").contentType(MediaType.APPLICATION_JSON)
						.content(BODY.formatted("none@example.com")))
				.andExpect(status().isNotFound());
	}
}
