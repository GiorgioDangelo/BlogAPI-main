package it.rdev.blog.api.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import it.rdev.blog.api.config.JwtRequestFilter;
import it.rdev.blog.api.config.JwtTokenUtil;
import it.rdev.blog.api.controller.dto.ArticoloDTO;
import it.rdev.blog.api.controller.dto.JwtResponse;
import it.rdev.blog.api.dao.UserDao;
import it.rdev.blog.api.dao.entity.User;
import it.rdev.blog.api.service.ArticoloUserDetailsService;



@RestController
public class ArticoloApiController {

	@Value("${jwt.header}")
	private String jwtHeader;
	@Autowired
	private JwtTokenUtil jwtUtil;
	@Autowired
	private UserDao userdao;
	
	@Autowired
	ArticoloUserDetailsService articoloservice;
	
	@RequestMapping(value = "/api/categoria", method = RequestMethod.GET)
	public ResponseEntity<?> getArticoli() throws Exception {
		List<String> nomi_categorie;
		nomi_categorie=articoloservice.getAllCategorie();
		//Qui gestire la risposta
		if(nomi_categorie!=null) {
		return ResponseEntity.ok(nomi_categorie);}
		else {
			return (ResponseEntity<?>) ResponseEntity.notFound();
		}
	}

	
	
	@RequestMapping(value = "/api/articolo", method = RequestMethod.POST)
	public ResponseEntity<?> saveArticoloBozza(@RequestBody ArticoloDTO articolo,@RequestHeader(name = "Authorization") String token) throws Exception {
		String username = null;
		if(token != null && token.startsWith("Bearer")) {
			token = token.replaceAll("Bearer ", "");
			username = jwtUtil.getUsernameFromToken(token);
		}
		User utente=userdao.findByUsername(username);
		return ResponseEntity.ok(articoloservice.save(articolo,utente));
	}
}
