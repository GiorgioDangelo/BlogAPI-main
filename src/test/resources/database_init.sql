CREATE TABLE IF NOT EXISTS users (
  id bigint unsigned NOT NULL AUTO_INCREMENT,
  username varchar(50) NOT NULL,
  password varchar(100) NOT NULL,
  PRIMARY KEY (id)
);
--
--
-- ddinuzzo/password02
INSERT INTO users
(username, password)
VALUES('ddinuzzo', '$2a$10$vj3PqvSqQSsLhknZpxU2oOIUOdmm6cpPu1shwcyXHVzba.xBWLe4K');
