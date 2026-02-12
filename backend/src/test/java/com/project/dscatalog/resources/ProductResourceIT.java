package com.project.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIT {

	@Autowired
	private MockMvc mockMvc;

	private Long existingId;
	private Long nonExistingId;
	private Long countTotalProducts;

	@BeforeEach
	void setUp() throws Exception {

		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L;
	}

	@Test
	public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
		ResultActions result = mockMvc
				.perform(get("/products?page=0&size=12&sort=name,asc").accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.totalElements").value(countTotalProducts));
		result.andExpect(jsonPath("$.content").exists());
		result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
		result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
		result.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
	}

	@Test
	public void findByIdShouldReturnProductWhenIdExists() throws Exception {

		ResultActions result = mockMvc.perform(get("/products/{id}", existingId).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").value(existingId));
		result.andExpect(jsonPath("$.name").exists());
	}

	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

	    ResultActions result = mockMvc.perform(
	            get("/products/{id}", nonExistingId)
	            .accept(MediaType.APPLICATION_JSON));

	    result.andExpect(status().isNotFound());
	}

	@Test
	public void insertShouldReturnCreated() throws Exception {

		String jsonBody = "{"
		        + "\"name\": \"New Product\","
		        + "\"description\": \"New Description\","
		        + "\"price\": 100.0,"
		        + "\"imgUrl\": \"\""
		        + "}";


	    ResultActions result = mockMvc.perform(
	            post("/products")
	            .content(jsonBody)
	            .contentType(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON));

	    result.andExpect(status().isCreated());
	    result.andExpect(jsonPath("$.id").exists());
	    result.andExpect(jsonPath("$.name").value("New Product"));
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

		String jsonBody = "{"
		        + "\"name\": \"New Product\","
		        + "\"description\": \"New Description\","
		        + "\"price\": 100.0,"
		        + "\"imgUrl\": \"\""
		        + "}";

	    ResultActions result = mockMvc.perform(
	            put("/products/{id}", nonExistingId)
	            .content(jsonBody)
	            .contentType(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON));

	    result.andExpect(status().isNotFound());
	}
	
	@Test
	public void deleteShouldReturnNoContentWhenIdExists() throws Exception {

	    ResultActions result = mockMvc.perform(
	            delete("/products/{id}", existingId));

	    result.andExpect(status().isNoContent());
	}

	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

	    ResultActions result = mockMvc.perform(
	            delete("/products/{id}", nonExistingId));

	    result.andExpect(status().isNotFound());
	}

}
