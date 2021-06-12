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
	private TagUserDetailsService service_tag;
	@Autowired
	private ArticoloDao articolodao;
	@Autowired
	private ApiController apiController;
	//Praticamente controllo l'utente se esiste e se a lui è associato ad un articolo 
	//in maniera tale che si possono aggiungere determinati tag a quell'articolo
	
	
	@RequestMapping(value = "/api/tag/{id_articolo}", method = RequestMethod.POST)
	public ResponseEntity<?> saveArticoloBozza(@PathVariable  Long id_articolo,@RequestBody TagsDTO tag_dto,
			@RequestHeader(name = "Authorization") String token) throws Exception {
		String username = apiController.controlloToken(token);
		//Quindi vado l'utente inserisce il tag in base al suo nome e all'id del suo articolo
		//facendo i dovuti controlli
		Articolo ok=articolodao.ricercaArticolo(username,id_articolo);
		if(ok!=null) {
			return ResponseEntity.ok(service_tag.save(tag_dto, ok));
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		
	}
	/*
	 * Restituisce la lista di tags presenti nel databaseStatus code restituiti:
	   ●200: se sono stati restituiti dei tags
	   ●404: se non è presente alcun tag all’interno del database
	 */
	@RequestMapping(value = "/api/tag", method = RequestMethod.GET)
	public ResponseEntity<?> getArticoli() throws Exception {
		List<String> nomi_tag;
		nomi_tag=service_tag.getAllTags();
		if(nomi_tag!=null) {
		return ResponseEntity.ok(nomi_tag);}
		else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
