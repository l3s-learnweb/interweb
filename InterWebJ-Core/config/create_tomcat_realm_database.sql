USE interwebj;

CREATE TABLE principals (
  id int(10) unsigned NOT NULL AUTO_INCREMENT,
  name varchar(20) NOT NULL,
  password varchar(32) NOT NULL,
  email varchar(40) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE roles (
  role varchar(20) NOT NULL,
  PRIMARY KEY (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE principals_roles (
  id int(10) unsigned NOT NULL,
  role varchar(20) NOT NULL,
  PRIMARY KEY (id, role),
  CONSTRAINT principals_roles_fk_1 FOREIGN KEY (id) REFERENCES principals (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT principals_roles_fk_2 FOREIGN KEY (role) REFERENCES roles (role)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE iwj_connectors (
  id int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  name VARCHAR(80) NOT NULL,
  api_key VARCHAR(255) NOT NULL,
  api_secret VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE iwj_principals_connectors (
  principal_id int(10) UNSIGNED NOT NULL,
  connector_id int(10) UNSIGNED NOT NULL,
  api_token VARCHAR(255)  NOT NULL,
  PRIMARY KEY (principal_id, connector_id),
  CONSTRAINT principals_connectors_fk1 FOREIGN KEY (principal_id) REFERENCES iwj_connectors (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT principals_connectors_fk2 FOREIGN KEY (connector_id) REFERENCES iwj_connectors (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET=utf8;



INSERT INTO principals (name, password, firstname, lastname, email) 
VALUES 	('guest', '12345', ''),
		('olex', '12345', 'druzhynin@gmail.com');

INSERT INTO roles (role)
VALUES 	('user'),
		('admin'),
		('manager');

INSERT INTO principals_roles 
SELECT id, role from principals, roles where name = 'olex' OR name='guest' and role = 'user';




COMMIT;