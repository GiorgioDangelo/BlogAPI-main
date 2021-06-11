# BlogAPI
Progetto base per l'esercitazione final del corso Java. Il progetto è sviluppato in Spring boot
e mette a disposizione delle API rest per registrare un utente ed autenticarlo.
L'autenticazione restituisce un token JWT che dovrà essere utilizzato per accedere alle
API RESTfull richieste dall'esercitazione.

### Requirements
Il progetto prevede la presenza di una server MySQL attivo e raggiungibile che al suo interno
abbia la tabella users creata con la seguente istruzione DDL:

```
CREATE TABLE users (
  id bigint unsigned NOT NULL AUTO_INCREMENT,
  username varchar(50) NOT NULL,
  password varchar(100) NOT NULL,
  PRIMARY KEY (id)
);
```

### API esposte
Il progetto base mette a disposizione le seguenti API:
* http://{hostname}:{port}/auth
* http://{hostname}:{port}/register

###### [POST] http://{hostname}:{port}/auth
Questa API accetta in input un oggetto JSON nel seguente formato:

```
{
	"username": "utente01",
	"password": "password01"
}
```
e, se l'utente è registrato alla piattaforma, viene restituito il seguente oggetto in output:

```
{
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkZGludXp6byIsImV4cCI6MTYyMzI4OTI ..."
}
```

contenente il token JWT da utilizzare per poter accedere alle risorse protette.


###### [POST] http://{hostname}:{port}/register
Questa API accetta in input un oggetto JSON nel seguente formato:

```
{
	"username": "utente01",
	"password": "password01"
}
```

e resituisce il nome dell'utente se la registrazione è andata a buon fine

```
{
    "username": "utente01"
}
```