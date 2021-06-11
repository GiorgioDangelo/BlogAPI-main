package it.rdev.blog.api.service;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import it.rdev.blog.api.controller.dto.ArticoloDTO;
import it.rdev.blog.api.dao.ArticoloDao;
import it.rdev.blog.api.dao.UserDao;
import it.rdev.blog.api.dao.entity.Articolo;
import it.rdev.blog.api.dao.entity.User;

@Service
public class ArticoloUserDetailsService {
	@Autowired 
	private ArticoloDao articoloDao;
	@Autowired 
	private UserDao userDao;
	
	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userDao.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("Utente non trovato per username: " + username);
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				new ArrayList<>());
	}
	
	public Articolo save(ArticoloDTO articolo,User utente_corrente) {
		Articolo newArticolo = new Articolo();
		Date date = new Date();
		newArticolo.setTesto(articolo.getTesto());
		newArticolo.setTitolo(articolo.getTitolo());
		newArticolo.setCategoria(articolo.getCategoria());
		newArticolo.setSottotitolo(articolo.getSottotitolo());
		newArticolo.setStato(0);
		newArticolo.setData_di_pubblicazione(date);
		newArticolo.setStruttura_user(utente_corrente);
		return articoloDao.save(newArticolo);
	}
	
	public Articolo update(ArticoloDTO articolo,User utente_corrente,Articolo articolo_selezionato) {
		Articolo newArticolo = new Articolo();
		Date date = new Date();
		newArticolo.setId(articolo_selezionato.getId());
		newArticolo.setTesto(articolo.getTesto());
		newArticolo.setTitolo(articolo.getTitolo());
		newArticolo.setCategoria(articolo.getCategoria());
		newArticolo.setSottotitolo(articolo.getSottotitolo());
		newArticolo.setStato(1);
		newArticolo.setData_di_pubblicazione(articolo_selezionato.getData_di_pubblicazione());
		newArticolo.setData_ultima_modifica(date);
		newArticolo.setStruttura_user(utente_corrente);
		return articoloDao.save(newArticolo);
	}
	
	public List<String> getAllCategorie() {
		List<Articolo> tutto=(List<Articolo>) articoloDao.findAll();
		List<String> nomi_categorie=new ArrayList<String>();
		if(tutto!=null) {
			for(int i=0;i<tutto.size();i++) {
				nomi_categorie.add(tutto.get(i).getCategoria());
			}
		}
				
		return nomi_categorie;
	}
	
	
	
}
