package com.todolist.task.controllers;

import java.io.IOException;

import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractControllerTest<Cont, Ex> {

	protected MockMvc mvc;
	
	protected void setup(Cont controller, Ex exceptionHandler) {
		mvc = MockMvcBuilders.standaloneSetup(controller)
				.setControllerAdvice(exceptionHandler)
				.build();
	}
	
	protected String convertMapToJson(Object mapObj) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(mapObj);
	}
	
	protected <T> T convertJsonToMap(String json, Class<T> class_obj) 
	throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(json, class_obj);
	}
	
	protected <T> T convertMVCResultToMap(MvcResult mvcResult, Class<T> class_obj) 
			throws JsonParseException, JsonMappingException, IOException {
			String data = mvcResult.getResponse().getContentAsString();
			return convertJsonToMap(data, class_obj);
	}
	
}
