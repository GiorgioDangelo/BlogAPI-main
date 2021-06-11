package it.rdev.blog.api.controller.dto;

import java.io.Serializable;

public class JwtResponse implements Serializable {

	private static final long serialVersionUID = -8091879091924046844L;
	private final String jwttoken;
    
	//Passo al costruttore di JWT il token 
	public JwtResponse(String jwttoken) {
		this.jwttoken = jwttoken;
	}
 
	public String getToken() {
		return this.jwttoken;
	}
}