package it.rdev.blog.api.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.rdev.blog.api.dao.entity.User;

@Repository
public interface UserDao extends CrudRepository<User, String> {
	
	// UserDao quindi implementa CrudRepository e inserendo la stringa ti trova
	// l'utente
	User findByUsername(String username);
	
}