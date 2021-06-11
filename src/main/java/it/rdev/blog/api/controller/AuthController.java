package it.rdev.blog.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import it.rdev.blog.api.config.JwtTokenUtil;
import it.rdev.blog.api.controller.dto.JwtRequest;
import it.rdev.blog.api.controller.dto.JwtResponse;
import it.rdev.blog.api.controller.dto.UserDTO;
import it.rdev.blog.api.service.BlogUserDetailsService;

@RestController
@CrossOrigin
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired  
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private BlogUserDetailsService userDetailsService;
     //aaa
	//Metodo post con il path auth ,prende in input un oggetto JwtRequest
	@RequestMapping(value = "/auth", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        //Praticamente dall'istanza authenticationRequest di JwtRequest praticamente
		//prende il nome e la password dal body e vede se sono stati formattati in maniera 
		//corretta sennò mi lancia un errore
		authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        
		// qui con il loadUserByUsername prende il nome e mi restituisce l'oggetto con quel
		//nome utente e vede se in realtà user è diverso da null quindi qui fa il controllo
		//effettivo per vedere se esiste l'utente?
		final UserDetails userDetails = userDetailsService
				.loadUserByUsername(authenticationRequest.getUsername());
        //arrivati qui significa che le credenziali sono corrette e genera il token
		final String token = jwtTokenUtil.generateToken(userDetails);
        
		//da una risposta con codice 200 restituiendo il token
		return ResponseEntity.ok(new JwtResponse(token));
	}
	//Qui è per registrare l'utente con richiesta POST
	//Prende quindi in input l'oggetto user del DTO 
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> saveUser(@RequestBody UserDTO user) throws Exception {
		//praticamente ritorna ok se il persist dell'utente ha avuto successo
		return ResponseEntity.ok(userDetailsService.save(user));
	}
     
	//Controlla se il nome utente e la password sono corretti  (e' una specie di query?)
	private void authenticate(String username, String password) throws Exception {
		try {//controlla se i dati sono scritti correttamente
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
}