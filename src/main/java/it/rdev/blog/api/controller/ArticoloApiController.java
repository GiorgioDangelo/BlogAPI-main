package it.rdev.blog.api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import it.rdev.blog.api.config.JwtTokenUtil;
import it.rdev.blog.api.controller.dto.ArticoloDTO;
import it.rdev.blog.api.dao.ArticoloDao;
import it.rdev.blog.api.dao.UserDao;
import it.rdev.blog.api.dao.entity.Articolo;
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
	private ArticoloDao articolodao;
	
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
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	
	//Inserimento nel database
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
	
	//Ricerca dell'api tramite l'id con il metodo get
	@RequestMapping(value = "/api/articolo/{id_articolo}", method = RequestMethod.GET)
	public ResponseEntity<?> recupero_di_un_singolo_articolo(@PathVariable Long id_articolo,
			@RequestHeader(name = "Authorization", required = false) String token) throws Exception {
		String username = null;
		if (token != null && token.startsWith("Bearer")) {
			token = token.replaceAll("Bearer ", "");
			username = jwtUtil.getUsernameFromToken(token);
		}

		Articolo controllo_articolo = articolodao.trovaID(id_articolo);

		Articolo controllo_articolo_con_utente = articolodao.ricercaArticolo(username, id_articolo);
		// Lo stato 0 identifica la bozza mentre 1 la pubblicazione dell'articolo
		// In questo controllo verifico che esiste l'articolo e che lo stato è 0 ossia è
		// una BOZZA
		if (controllo_articolo != null && controllo_articolo.getStato() == 0) {
			return ResponseEntity.ok(controllo_articolo);
		}
		// se l'articolo esiste ed è stato pubblicato e chi lo sta cercando è anche
		// l'autore allora puoi visualizzarlo
		if (controllo_articolo != null && controllo_articolo.getStato() == 1 && controllo_articolo_con_utente != null) {
			return ResponseEntity.ok(controllo_articolo);
		} else {
			// se non è verificata nessuna delle condizioni precendeti lancia il not found
			// con errore 404
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	//Ricerca articoli
	@RequestMapping(value = "/api/articolo", method = RequestMethod.GET)
	public ResponseEntity<?> ricercaArticoli(@RequestHeader(name = "Authorization", required = false) String token) throws Exception {
		String username = null;
		if (token != null && token.startsWith("Bearer")) {
			token = token.replaceAll("Bearer ", "");
			username = jwtUtil.getUsernameFromToken(token);
		}
		//Se non c'è nessun utente loggato restituisci solo gli articoli con stato Pubblicato ossia 1 mentre 0 corrisponde alla bozza
		List <Articolo> articoli_pubblicati=articolodao.trovaArticoliPubblicati(1);	
		List <Articolo> articoli_bozza;
		//se l'utente non è loggato ed esistono articoli pubblicati falli vedere al client restituendo lo stato 200
		if(username==null && articoli_pubblicati!=null) {
			return ResponseEntity.ok(articoli_pubblicati);
		}
		//se invece l'utente è loggato oltre a restituire tutti gli articoli pubblicati può vedere anche i suoi articoli in stato Bozza
		//se presenti
		if (username!=null) {
			//Con questa query vado a selezionare l'utente corrente che ha articoli nello stato Bozza 
			articoli_bozza=articolodao.trovaArticoliBozzaUtente(username,0);
			//Praticamente qui faccio un controllo e vedo se sia articoli_bozza che pubblicati sono valorizzati ,se è cosi praticamente
			//inserisco in una lista i valori dell'altro in questo modo da ritornare un'unica lista al client avente i valori degli articoli
			//pubblicati + gli articoli bozza dell'utente
			if(articoli_bozza!=null && articoli_pubblicati!=null) {
				for (int i=0;i<articoli_bozza.size();i++) {
					articoli_pubblicati.add(articoli_bozza.get(i));
				}
				return ResponseEntity.ok(articoli_pubblicati);
			}
			//Se una delle due liste non è valorizzata può significare 2 cose:
			//1) l'utente corrente non ha articoli bozza quindi restituisco al client solo gli articoli con stato Pubblicato (1)
			//2)Può essere che non esistono articoli pubblicati ma che l'utente ha scritto degli articoli con Stato Bozza quindi
			//Praticamente l'utente visualizzerà solo i suoi articoli in quanto non sono presenti articoli pubblicati
			if(articoli_bozza==null) {
				return ResponseEntity.ok(articoli_pubblicati);
			}
			else {
				return ResponseEntity.ok(articoli_bozza);
			}
		}
		// se arrivo a questo punto significa che non è loggato nessun utente e non ci sono articoli pubblicati e restituisce l'errore 404
		else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
		}
		
		
	}
	
	
}
