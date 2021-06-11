package it.rdev.blog.api.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.rdev.blog.api.dao.entity.Articolo;
import it.rdev.blog.api.dao.entity.User;

public interface ArticoloDao extends CrudRepository<Articolo, Integer>{
	@Query("Select a from Articolo a JOIN a.struttura_user c WHERE c.username= :valore and a.id= :valore1")
	Articolo ricercaArticolo(@Param("valore")String ricerca,@Param("valore1")Long id);
	
	@Query("Select a from Articolo a WHERE a.id= :valore")
	Articolo trovaID(@Param("valore")Long id);
	@Query("Select a from Articolo a WHERE a.stato= :valore")
	List<Articolo> trovaArticoliPubblicati(@Param("valore")Integer id);
	
	@Query("Select a from Articolo a JOIN a.struttura_user c WHERE c.username= :valore and a.stato= :valore1")
	List<Articolo> trovaArticoliBozzaUtente(@Param("valore")String ricerca,@Param("valore1")Integer id);
	
	@Query("Select a from Articolo a JOIN a.struttura_user c WHERE c.username= :valore and a.stato= :valore1 and a.id= :valore2")
	Articolo trovaArticoloUtenteBozza(@Param("valore")String ricerca,@Param("valore1")Integer stato,@Param("valore2")Long id);
	
	
}
