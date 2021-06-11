package it.rdev.blog.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestApiController {
       
	@GetMapping({ "/api/test" }) 
	public String get() {
		return "Risorsa Accesibile";
	}
	
	@PostMapping({ "/api/test" })
	public String post() {
		return "Risorsa Protetta";
	}

}