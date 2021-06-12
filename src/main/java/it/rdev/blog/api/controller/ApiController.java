package it.rdev.blog.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import it.rdev.blog.api.config.JwtTokenUtil;

@RestController
public class ApiController {
	@Autowired
	private JwtTokenUtil jwtUtil;

	public String controlloToken(String token) {
		String username = null;
		if (token != null && token.startsWith("Bearer")) {
			token = token.replaceAll("Bearer ", "");
			username = jwtUtil.getUsernameFromToken(token);
			return username;
		} else {
			return username;
		}
	}
}
