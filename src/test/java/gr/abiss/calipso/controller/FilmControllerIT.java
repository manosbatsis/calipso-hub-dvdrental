package gr.abiss.calipso.controller;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gr.abiss.calipso.model.Film;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.web.spring.PageImpl;

public class FilmControllerIT {
	private static final Logger LOGGER = LoggerFactory.getLogger(FilmControllerIT.class);

	@Test
	public void testSearch() throws Exception {
		try {
			URL url = new URL("http://localhost:8080/calipso/api/rest/films");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException(" HTTP error code : " + conn.getResponseCode());
			}

			Scanner scan = new Scanner(url.openStream());
			String entireResponse = new String();
			while (scan.hasNext())
				entireResponse += scan.nextLine();


			scan.close();
			ObjectMapper objectMapper=new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Page<Film> page = getPageFromJson(entireResponse, objectMapper, Film.class);
			for(Film film : page.getContent()){
				LOGGER.info("Film : " + film.getTitle());
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
	}

	private Page<Film> getPageFromJson(String entireResponse, ObjectMapper objectMapper, Class resourceClass)
			throws IOException, JsonParseException, JsonMappingException {
		Page<Film> page = objectMapper.readValue(entireResponse, 
				objectMapper.getTypeFactory().constructParametricType(PageImpl.class, resourceClass));
		return page;
	}

}