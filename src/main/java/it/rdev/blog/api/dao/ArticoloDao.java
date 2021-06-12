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
	//trovo l'articolo tramite l'id
	@Query("Select a from Articolo a WHERE a.id= :valore")
	Articolo trovaID(@Param("valore")Long id);
	//prendo gli articoli con un determinato stato (bozza o pubblicato)
	@Query("Select a from Articolo a WHERE a.stato= :valore")
	List<Articolo> trovaArticoliPubblicati(@Param("valore")Integer id);
	
	//trovo l'articolo dal nome utente e lo stato 
	@Query("Select a from Articolo a JOIN a.struttura_user c WHERE c.username= :valore and a.stato= :valore1")
	List<Articolo> trovaArticoliBozzaUtente(@Param("valore")String ricerca,@Param("valore1")Integer id);
	
	//seleziono l'utente con l'articolo associato e con lo stato dell'articolo
	@Query("Select a from Articolo a JOIN a.struttura_user c WHERE c.username= :valore and a.stato= :valore1 and a.id= :valore2")
	Articolo trovaArticoloUtenteBozza(@Param("valore")String ricerca,@Param("valore1")Integer stato,@Param("valore2")Long id);
	
	
	//qui seleziono l'articolo con l'utente associato
	@Query("Select a from Articolo a JOIN a.struttura_user c WHERE c.username= :valore and a.id= :valore2")
	Articolo articoloUtente(@Param("valore")String ricerca,@Param("valore2")Long id);
	
	//filtrare i risultati
	@Query("Select a from Articolo a WHERE a.titolo like :ricerca OR a.sottotitolo like :ricerca OR a.testo like :ricerca")
	List<Articolo> ricercaContenuti(@Param("ricerca")String ricerca);
	//Trovate l'articolo con un determinato id e stato pubblicato
	@Query("Select a from Articolo a WHERE a.id= :valore and a.stato=1")
	Articolo articoloTramiteIdConStatoPubblicato(@Param("valore")Long id);
	//Ricerca categoria
	@Query("Select a from Articolo a WHERE a.categoria= :valore")
	List<Articolo> ricercaCategoria(@Param("valore")String categoria);
	//ricerca autore
	@Query("Select a from Articolo a LEFT JOIN a.struttura_user c WHERE c.username= :valore")
	List<Articolo> ricercaAutore(@Param("valore")String autore);
	
	//Ricerca tramite il Tag
	@Query("Select a from Articolo a JOIN a.struttura_user c WHERE a.id= :valore2")
	List<Articolo> articoloTag(@Param("valore")String ricerca);
}
