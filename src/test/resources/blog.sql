create database blog;

use blog;

CREATE TABLE users (
  username varchar(50) NOT null,
  password varchar(100) NOT NULL,
  PRIMARY KEY (username)
);

CREATE TABLE articolo (
  id bigint unsigned NOT NULL AUTO_INCREMENT,
  testo varchar(200) NOT null ,
  categoria varchar(50) NOT NULL,
  titolo varchar(50) NOT null unique,
  sottotitolo varchar(50),
  id_users varchar(50) not Null,
  stato int not null,
  data_di_pubblicazione datetime not null,
  data_ultima_modifica datetime ,
  PRIMARY KEY (id)
);

CREATE TABLE tags (
  id bigint unsigned NOT NULL AUTO_INCREMENT,
  tag varchar(50), 
  id_articolo bigint unsigned not null,
  PRIMARY KEY (id)
);

ALTER TABLE `articolo`
  ADD CONSTRAINT `articolo_ibfk` FOREIGN KEY (`id_users`) REFERENCES `users` (`username`)
 ON DELETE CASCADE;

ALTER TABLE `tags`
  ADD CONSTRAINT `tags_ibfk` FOREIGN KEY (`id_articolo`) REFERENCES `articolo` (`id`)
 ON DELETE CASCADE;



