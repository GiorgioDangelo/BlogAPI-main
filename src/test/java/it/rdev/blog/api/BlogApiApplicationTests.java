package it.rdev.blog.api;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment=RANDOM_PORT)
@Sql({"/database_init.sql"})
class BlogApiApplicationTests {
	
	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private WebTestClient client;
	
	@BeforeAll
    public static void setup() { }

	@Test
	void testResourcesWithoutUser() {
		/**
		 * Provo a recuperare una risorsa non protetta
		 * senza effettuare la login.
		 */
		client.get().uri("/api/test")
			.exchange()
			.expectStatus().isOk();
		
		/**
		 * Provo a recuperare una risorsa protetta
		 * senza effettuare la login
		 */
		client.post().uri("/api/test")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus()
			// Mi aspetto che venga restituito isUnauthorized
			.isUnauthorized();
		
	}
	
	@Test
	void testAuth() {
		/*
		 * Provo a registrare un nuovo utente
		 */
		client.post().uri("/register")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue("{ \"username\": \"utenteTest01\", \"password\": \"password10\" }")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.jsonPath("$.username").isEqualTo("utenteTest01");
		
		
		byte[] response = client.post().uri("/auth")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue("{ \"username\": \"utenteTest01\", \"password\": \"password10\" }")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.jsonPath("$.token").exists()
			.returnResult().getResponseBodyContent();
		
		String textResp = new String(response);
		log.info("RESPONSE ----> " + textResp);
		int lastPos = textResp.lastIndexOf("\"");
		if(lastPos >= 0) {
			textResp = textResp.substring(0, lastPos);
			lastPos = textResp.lastIndexOf("\"");
			if(lastPos >= 0) {
				String token = textResp.substring(lastPos + 1);
				log.info("TOKEN ----> " + token);
				
				client.post().uri("/api/test")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + token)
					.exchange()
					.expectStatus()
					// Mi aspetto che venga restituito isOk
					// dato che ho inviato il token di autenticazione
					.isOk();
					// .andExpect(jsonPath("$.length()", is(2)))
			}
		}
	}

}
