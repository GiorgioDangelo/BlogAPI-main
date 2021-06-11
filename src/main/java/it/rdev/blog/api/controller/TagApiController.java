package it.rdev.blog.api.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import it.rdev.blog.api.config.JwtTokenUtil;
import it.rdev.blog.api.controller.dto.TagsDTO;
import it.rdev.blog.api.dao.ArticoloDao;
import it.rdev.blog.api.dao.entity.Articolo;
import it.rdev.blog.api.service.TagUserDetailsService;


@RestController
public class TagApiController {
	
	@Autowired
	private JwtTokenUtil jwtUtil;
	@Autowired
	private TagUserDetailsService service_tag;
	@Autowired
	private ArticoloDao articolodao;
	//Praticamente controllo l'utente se esiste e se a lui è associato ad un articolo 
	//in maniera tale che si possono aggiungere determinati tag a quell'articolo
	@RequestMapping(value = "/api/tag/{id_articolo}", method = RequestMethod.POST)
	public ResponseEntity<?> saveArticoloBozza(@PathVariable  Long id_articolo,@RequestBody TagsDTO tag_dto,
			@RequestHeader(name = "Authorization") String token) throws Exception {
		String username = null;
		if(token != null && token.startsWith("Bearer")) {
			token = token.replaceAll("Bearer ", "");
			username = jwtUtil.getUsernameFromToken(token);
		}
		//Quindi vado l'utente inserisce il tag in base al suo nome e all'id del suo articolo
		//facendo i dovuti controlli
		Articolo ok=articolodao.ricercaArticolo(username,id_articolo);
		if(ok!=null) {
			return ResponseEntity.ok(service_tag.save(tag_dto, ok));
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		
	}
	//Questa è l'api che restituisce i tag,se effettivamente ci sono tag il codice di risposta sarà 200
	//se non ci sono dati nel database ,lancia l'errore 404 not found
	@RequestMapping(value = "/api/tag", method = RequestMethod.GET)
	public ResponseEntity<?> getArticoli() throws Exception {
		List<String> nomi_tag;
		nomi_tag=service_tag.getAllTags();
		//Qui gestire la risposta
		if(nomi_tag!=null) {
		return ResponseEntity.ok(nomi_tag);}
		else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
