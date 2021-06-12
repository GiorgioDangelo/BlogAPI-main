package it.rdev.blog.api.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
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
	private UserDao userdao;
	@Autowired
	private ArticoloDao articolodao;
	@Autowired
	private ApiController apiController;

	@Autowired
	ArticoloUserDetailsService articoloservice;

	/*
	 * Restituisce la lista di categorie presenti nel database Status code
	 * restituiti: ●200: se sono state restituite delle categorie ●404: se non è
	 * presente alcuna categoria all’interno del database
	 */
	@RequestMapping(value = "/api/categoria", method = RequestMethod.GET)
	public ResponseEntity<?> getArticoli() throws Exception {
		List<String> nomi_categorie;
		// Ho una query che mi restituisce tutte le categorie
		nomi_categorie = articoloservice.getAllCategorie();
		// Se ci sono elementi nella lista significa che ci sono categorie quindi
		// mostrale al client e stampa il code 200 tutt'ok
		if (nomi_categorie != null) {
			return ResponseEntity.ok(nomi_categorie);
		} else {
			// In questo caso significa che non è stata trovate nessuna categoria quindi la
			// lista è vuota e lancia il code 404
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	/*
	 * Il servizio permetterà l’inserimento di un articolo ad un giornalista registrato. Dovrà prendere in input un articolo in formato 
	 * JSON compilato in ogni sua parte ed indicare all’utente l’avvenuto inserimento senza restituire alcun valore in response.
	 *  L’articolo inserito è sempre in bozza, il passaggio in stato pubblicato sarà effettuato da un altro servizio.
	 Status code restituiti:
	 ●	204: se l’articolo è stato inserito correttamente
	 ●	400: se uno dei parametri passati in input non è valorizzato o corretto
	 ●	401: se un utente non loggato prova ad effettuare l’inserimento di un articolo

	 */
	@RequestMapping(value = "/api/articolo", method = RequestMethod.POST)
	public ResponseEntity<?> saveArticoloBozza(@RequestBody ArticoloDTO articolo,
			@RequestHeader(name = "Authorization") String token) throws Exception {
		String username = apiController.controlloToken(token);
		User utente = userdao.findByUsername(username);
		if (articolo.getSottotitolo() == null || articolo.getTesto() == null || articolo.getTitolo() == null
				|| articolo.getCategoria() == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		if(utente!=null) {
			articoloservice.save(articolo, utente);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		else {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
	}

	/*
	 * Restituisce un singolo articolo in formato JSON identificato dall’id passato
	 * nella path variable<:id>. L’endpoint è raggiungibile da tutti gli utenti
	 * (registrati ed anonimi), se l’id è relativo ad un articolo in stato bozza
	 * sarà restituito solo all’autore gli altri utenti otterranno uno status code
	 * 404.Status code restituiti: ●200:se l’id passato come parametro è relativo ad
	 * un articolo in stato pubblicato o ad un articolo in stato bozza e l’utente
	 * che lo richiedene è l’autore; ●404:se l’id passato non corrisponde a nessun
	 * articolo o se l’articolo che identifica è in stato bozza ma l’utente loggato
	 * non ne è l’autore oppure è un utente anonimo.
	 */
	@RequestMapping(value = "/api/articolo/{id_articolo}", method = RequestMethod.GET)
	public ResponseEntity<?> recupero_di_un_singolo_articolo(@PathVariable Long id_articolo,
			@RequestHeader(name = "Authorization", required = false) String token) throws Exception {

		String username = apiController.controlloToken(token);
		Articolo controllo_articolo = articolodao.trovaID(id_articolo);
		// se non esiste l'articolo lancia direttamente il not_found
		if (controllo_articolo == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		// Qui trovo gli articoli con stato Pubblicato ossia con stato 1
		Articolo articolo_con_stato_pubblicato = articolodao.articoloTramiteIdConStatoPubblicato(id_articolo);
		// se l'utente non è loggato e esiste l'articolo con quell'id ed è nello stato
		// pubblicato mostra l'articolo all'utente anonimos
		if (username == null && articolo_con_stato_pubblicato != null) {
			return ResponseEntity.ok(articolo_con_stato_pubblicato);
		}
		// In questo caso significa che l'articolo effettivamente esiste,ma l'utente non
		// è loggato ed l'articolo si trova in stato Bozza
		if (username == null && articolo_con_stato_pubblicato == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		//
		Articolo controllo_articolo_con_utente = articolodao.ricercaArticolo(username, id_articolo);
		// Lo stato 0 identifica la bozza mentre 1 la pubblicazione dell'articolo
		// In questo controllo verifico che c'è un associazione tra l'autore e
		// l'articolo e il suo stato è bozza quindi
		// significa che effettivamente l'utente loggato è il creatore dell'articolo che
		// si trova nello stato bozza
		if (controllo_articolo_con_utente != null && controllo_articolo.getStato() == 0) {
			return ResponseEntity.ok(controllo_articolo_con_utente);
		}
		// In questo caso significa che non c'è un associazione tra l'utente e
		// l'articolo ossia che l'utente è loggato ma non è il
		// proprietario dell'articolo che sta cercando quindi se lo stato dell'articolo
		// che sta cercando si trova su 1 signfica che può
		// vederlo perchè è stato pubblicato in caso contrario significa che l'utente è
		// loggato non è il proprietario dell'articolo
		// e l'articolo si trova nello stato bozza quindi lancia l'errore 404 not_found
		if (controllo_articolo_con_utente == null && controllo_articolo.getStato() == 1) {
			return ResponseEntity.ok(controllo_articolo);
		} else {

			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	/*
	 * Restituisce una lista di articoli eventualmente paginata in formato JSON.
	L’endpoint è raggiungibile da tutti gli utenti (registrati ed anonimi). Gli utenti anonimi otterranno in output solo gli 
	articoli in stato pubblicato mentre gli utenti loggati, oltre agli articoli in stato pubblicato, riceveranno anche i propri articoli 
	in stato bozza.
	Il servizio dovrà permettere di filtrare i risultati per testo (dovranno essere passati almeno 3 caratteri) cercando 
	il testo passato come parametro nel titolo, sottotitolo e testo dell’articolo in OR.
	Dovrà essere possibile ricercare un articolo anche per id, categoria, tag o autore.
	Se vengono passati più filtri di ricerca tra i precedenti dovranno essere utilizzati in AND.
	Status code restituiti:
	●200: se la ricerca produce risultati
	●400: se uno dei parametri passati in input non è formalmente corretto 
	●404: se la ricerca non produce alcun risultato

	 */
	@RequestMapping(value = "/api/articolo", method = RequestMethod.GET)
	public ResponseEntity<?> ricercaArticoli(@RequestParam(name="titolo",required=false) String titolo,@RequestParam(name="id",required=false) Long id,
			@RequestParam(name="categoria",required=false) String categoria,@RequestParam(name="autore",required=false) String autore,
			@RequestHeader(name = "Authorization", required = false) String token)
			throws Exception {
		String username = apiController.controlloToken(token);
		List<Articolo>lista_completa=new ArrayList<Articolo>();
		//Ricerca per titolo sottotitolo o testo dell'articolo
		List<Articolo> ricercaContenuti;
		if(titolo!=null && titolo.length()>=3) {
			ricercaContenuti=articolodao.ricercaContenuti(titolo);
			if(ricercaContenuti!=null) {
				for (int i=0;i<ricercaContenuti.size();i++) {
					lista_completa.add(ricercaContenuti.get(i));
				}
			}
		}
		//Ricerca per id
		Articolo articolo_specifico=articolodao.trovaID(id);
		if(articolo_specifico!=null) {
			lista_completa.add(articolo_specifico);
		}
		//Ricerca per categoria
		List<Articolo> lista_categoria=articolodao.ricercaCategoria(categoria);
		if(lista_categoria!=null) {
			for (int i=0;i<lista_categoria.size();i++) {
				lista_completa.add(lista_categoria.get(i));
			}
		}
		//Ricerca autore
		List<Articolo> lista_autore=articolodao.ricercaAutore(autore);
		System.out.println(lista_autore);
		if(lista_autore!=null) {
			for (int i=0;i<lista_autore.size();i++) {
				lista_completa.add(lista_autore.get(i));
			}
		}
		
		
		//Ritorna la lista completa se sono stati inseriti dei parametris
		if(lista_completa!=null) {
			ResponseEntity.ok(lista_completa);
		}
		

		// Se non c'è nessun utente loggato restituisci solo gli articoli con stato
		// Pubblicato ossia 1 mentre 0 corrisponde alla bozza
		List<Articolo> articoli_pubblicati = articolodao.trovaArticoliPubblicati(1);
		List<Articolo> articoli_bozza;

		// se l'utente non è loggato ed esistono articoli pubblicati falli vedere al
		// client restituendo lo stato 200
		if (username == null && articoli_pubblicati != null) {
			return ResponseEntity.ok(articoli_pubblicati);
		}
		// se invece l'utente è loggato oltre a restituire tutti gli articoli pubblicati
		// può vedere anche i suoi articoli in stato Bozza
		// se presenti
		if (username != null) {
			// Con questa query vado a selezionare l'utente corrente che ha articoli nello
			// stato Bozza
			articoli_bozza = articolodao.trovaArticoliBozzaUtente(username, 0);
			// Praticamente qui faccio un controllo e vedo se sia articoli_bozza che
			// pubblicati sono valorizzati ,se è cosi praticamente
			// inserisco in una lista i valori dell'altro in questo modo da ritornare
			// un'unica lista al client avente i valori degli articoli
			// pubblicati + gli articoli bozza dell'utente
			if (articoli_bozza != null && articoli_pubblicati != null) {
				for (int i = 0; i < articoli_bozza.size(); i++) {
					articoli_pubblicati.add(articoli_bozza.get(i));
				}
				return ResponseEntity.ok(articoli_pubblicati);
			}
			// Se una delle due liste non è valorizzata può significare 2 cose:
			// 1) l'utente corrente non ha articoli bozza quindi restituisco al client solo
			// gli articoli con stato Pubblicato (1)
			// 2)Può essere che non esistono articoli pubblicati ma che l'utente ha scritto
			// degli articoli con Stato Bozza quindi
			// Praticamente l'utente visualizzerà solo i suoi articoli in quanto non sono
			// presenti articoli pubblicati
			if (articoli_bozza == null) {
				return ResponseEntity.ok(articoli_pubblicati);
			} else {
				return ResponseEntity.ok(articoli_bozza);
			}
		}
		// se arrivo a questo punto significa che non è loggato nessun utente e non ci
		// sono articoli pubblicati e restituisce l'errore 404
		else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	/*
	 * Il servizio
	 * dovrà permettere l’update di un articolo passando in input un articolo
	 * informato JSON. L’aggiornamento dovrà essere consentito solo all’autore
	 * dell’articolo.Status code restituiti: ●204: se l’operazione di update va a
	 * buon fine ●400: se sono presenti parametri non formalmente corretti ●401: se
	 * un utente non loggato prova ad effettuarel’update di un determinato articolo
	 * ●403: se un utente loggato che non è l’autore dell’articolo prova ad
	 * effettuarne l’update ●404: se l’id passato in input non appartiene ad
	 * alcun articolo
	 */
	@RequestMapping(value = "/api/articolo/{id_articolo}", method = RequestMethod.PUT)
	public ResponseEntity<?> modificaArticoli(@RequestBody ArticoloDTO articolo, @PathVariable Long id_articolo,
			@RequestHeader(name = "Authorization", required = false) String token) throws Exception {
		String username = apiController.controlloToken(token);
		// se l'utente non è loggato invia l'errore 401
		if (username == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		User utente = userdao.findByUsername(username);
		Articolo check_articolo = articolodao.trovaID(id_articolo);
		// se l'articolo con questo id non esiste lancia l'errore 404 not found
		if (check_articolo == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		// Se uno di questi valori non è stato valorizzato nel body lancia l'errore 400
		if (articolo.getSottotitolo() == null || articolo.getTesto() == null || articolo.getTitolo() == null
				|| articolo.getCategoria() == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		// Controllo all'inzio se l'utente è loggato ,poi vedo se l'id passato nell'api
		// corrisponde ad un suo articolo che si trova
		// nello stato di Bozza se è vero ,vado a fare l'update dell'articolo e
		// aggiornato lo stato con Pubblicato quindi aggiornado
		// lo stato ad 1 e torna il codice 204
		Articolo articolo_selezionato = articolodao.trovaArticoloUtenteBozza(username, 0, id_articolo);
		// significa che c'è un utente loggato,esiste quell'id ma l'utente non è il
		// proprietario di quell'articolo 403
		if (articolo_selezionato == null) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} // In questo caso significa che l'id corrisponde ad un articolo e questo
			// articolo corrisponde all'utente loggato e il suo stato è 0
			// cioè è un bozza quindi avviene l'update e ritorna lo staato di successo 204
		else {
			articoloservice.update(articolo, utente, articolo_selezionato);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}

	/*
	 * Il servizio elimina un articolo presente all’interno del
	 * database.L’eliminazione è consentita solo all’autore dell’articolo dopo aver
	 * effettuato la login.Status code restituiti: ●204: se l’eliminazione va a buon
	 * fine ●401: se un utente non loggato prova ad effettuarel’eliminazione di un
	 * articolo ●403: se l’utente loggato non è l’autore dell’articoloche cerca di
	 * eliminare ●404: se l’id passato in input non è associato ad alcunarticolo
	 * presente nel database
	 */
	@RequestMapping(value = "/api/articolo/{id_articolo}", method = RequestMethod.DELETE)
	public ResponseEntity<?> eliminaArticolo(@PathVariable Long id_articolo,
			@RequestHeader(name = "Authorization", required = false) String token) throws Exception {
		String username = apiController.controlloToken(token);
		// se l'utente non è loggato invia l'errore 401
		if (username == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		Articolo check_articolo = articolodao.trovaID(id_articolo);
		// se l'articolo con questo id non esiste lancia l'errore 404 not found
		if (check_articolo == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		Articolo utente_associato_all_articolo = articolodao.articoloUtente(username, id_articolo);
		// se è null significa che l'utente non corrisponde all'id quindi non è
		// autorizzato 403
		if (utente_associato_all_articolo == null) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);

		}
		// se invece esiste l'utente associato all'articolo esiste allora elimino
		// l'articolo 204
		else {
			articolodao.delete(utente_associato_all_articolo);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}

}
