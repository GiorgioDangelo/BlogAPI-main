package it.rdev.blog.api.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.rdev.blog.api.controller.dto.UserDTO;
import it.rdev.blog.api.dao.UserDao;
import it.rdev.blog.api.dao.entity.User;

@Service
public class BlogUserDetailsService implements UserDetailsService {
	
	@Autowired //l'UserDao estende crudRepository e ha il metodo
	//User findByUsername(String username);
	private UserDao userDao;

	@Autowired //PasswordEncoder è un'interfaccia di spring e serve per criptare la password
	private PasswordEncoder bcryptEncoder;

	@Override  //questo metodo prende il nome come input,fa una ricerca nel database
	//per vedere se lo trova e se lo trova restituisce un oggetto di tipo utente
    
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userDao.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("Utente non trovato per username: " + username);
		}
		//??????????????????????????????????????
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				new ArrayList<>());
	}
	
	//Crea un oggetto utente gli setta il nome e la password e viene chiamato userdato
	// e con il save praticamente fa il persist quindi lo inserisce nel database
	public User save(UserDTO user) {
		User newUser = new User();
		newUser.setUsername(user.getUsername());
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		return userDao.save(newUser);
	}
}