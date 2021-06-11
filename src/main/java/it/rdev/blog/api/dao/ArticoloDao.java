package it.rdev.blog.api.dao;

import org.springframework.data.repository.CrudRepository;

import it.rdev.blog.api.dao.entity.Articolo;

public interface ArticoloDao extends CrudRepository<Articolo, Integer>{
	

}
