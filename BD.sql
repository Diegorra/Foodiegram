drop trigger if exists ValoracioMediaD;
drop trigger if exists ValoracioMediaI;
drop trigger if exists ValoracioMediaU;
drop event if exists borrarCosasExpiradas;
drop trigger if exists updateOnUnsubscribe;


drop table if exists amigo;
drop table if exists comentario;
drop table if exists mensaje;
drop table if exists meetup;
drop table if exists evento;
drop table if exists patrocinio;
drop table if exists colaborador;
drop table if exists valoracion;
drop table if exists publicacion;
drop table if exists verifytoken;
drop table if exists jwtoken;
drop table if exists refreshtoken;
drop table if exists numvalpubli;
drop table if exists usuario_baneado;
drop table if exists usuario;

CREATE TABlE usuario (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL,
    passwd VARCHAR(256) NOT NULL,
    email VARCHAR(256) NOT NULL,
    image VARCHAR(256),
  	enabled boolean NOT NULL,
  	role ENUM('ROLE_ADMIN', 'ROLE_MOD', 'ROLE_USER', 'ROLE_COL'),
    PRIMARY KEY (id),
    UNIQUE (email),
  	UNIQUE (name)
);



CREATE TABLE publicacion (
    id INT NOT NULL AUTO_INCREMENT,
    iduser INT NOT NULL,
    text VARCHAR(256) NOT NULL,
    image VARCHAR(256),
    fecha DATE,
  	pais VARCHAR(256),
  	ciudad VARCHAR(256),
  	media Float,
  	numerototalval Int,
    PRIMARY KEY (id),
    FOREIGN KEY (iduser) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE comentario (
    id INT NOT NULL AUTO_INCREMENT,
    idpubli INT NOT NULL,
    iduser INT NOT NULL,
    text VARCHAR(256) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (idpubli) REFERENCES publicacion(id) ON DELETE CASCADE,
    FOREIGN KEY (iduser) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE valoracion (
    idpubli INT NOT NULL,
    iduser INT NOT NULL,
    punt INT NOT NULL,
    PRIMARY KEY (idpubli, iduser),
    FOREIGN KEY (idpubli) REFERENCES publicacion(id) ON DELETE CASCADE,
    FOREIGN KEY (iduser) REFERENCES usuario(id)
);


CREATE TABLE mensaje (
    id INT NOT NULL AUTO_INCREMENT,
    iduser1 INT NOT NULL,
    iduser2 INT NOT NULL,
    Text VARCHAR(256),
    PRIMARY KEY (id),
    FOREIGN KEY (iduser1) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (iduser2) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE amigo (
    iduser1 INT NOT NULL,
    iduser2 INT NOT NULL,
    PRIMARY KEY (iduser1, iduser2),
    FOREIGN KEY (iduser1) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (iduser2) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE colaborador (
    id INT NOT NULL,
    origin VARCHAR(256),
    type VARCHAR(256) NOT NULL,
    pais VARCHAR(256) NOT NULL,
    ciudad VARCHAR(256) NOT NULL,
	calle VARCHAR(256) NOT NULL,
    vip BOOLEAN NOT NULL,
    money FLOAT DEFAULT 0.0,
    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE evento (
    id INT NOT NULL AUTO_INCREMENT,
    idcolab INT NOT NULL,
    text VARCHAR(256) NOT NULL,
    image VARCHAR(256),
    endtime DATE NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (idcolab) REFERENCES colaborador(id)
);

CREATE TABLE meetup (
    id INT NOT NULL,
    iduser INT NOT NULL,
    PRIMARY KEY (id, iduser),
    FOREIGN KEY (id) REFERENCES evento(id) ON DELETE CASCADE,
    FOREIGN KEY (iduser) REFERENCES usuario(id) ON DELETE CASCADE
);


CREATE TABLE verifytoken (
    id INT NOT NULL AUTO_INCREMENT,
    email VARCHAR(256) NOT NULL,
  	token INT NOT NULL,
  	expiredate datetime,
    PRIMARY KEY (id),
    FOREIGN KEY (email) REFERENCES usuario(email) ON DELETE CASCADE,
  	UNIQUE(token),
  	UNIQUE(email)
  	
);

CREATE TABLE jwtoken (
  	id INT NOT NULL AUTO_INCREMENT,
  	userid INT NOT NULL,
  	expiredate datetime,
 	PRIMARY KEY (id),
  	FOREIGN KEY (userid) REFERENCES usuario(id) ON DELETE CASCADE,
  	UNIQUE(userid)
  
 );


CREATE TABLE refreshtoken(
	id INT NOT NULL AUTO_INCREMENT,
  	userid INT NOT NULL,
  	expiredate datetime,
 	PRIMARY KEY (id),
  	FOREIGN KEY (userid) REFERENCES usuario(id) ON DELETE CASCADE,
  	UNIQUE(userid)
);


create table numvalpubli (
    iduser INT NOT NULL,
    numval INT NOT NULL,
    numpubli INT NOT NULL,
    PRIMARY KEY (iduser),
    FOREIGN KEY (iduser) REFERENCES usuario(id) ON DELETE CASCADE
);


CREATE TABLE usuario_baneado(
    id INT NOT NULL,
     fecha datetime,
    PRIMARY KEY (id),
       FOREIGN KEY (id) REFERENCES usuario(id) ON DELETE CASCADE 

);

CREATE TABLE patrocinio(
    id INT NOT NULL,
    endtime DATE NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES colaborador(id) ON DELETE CASCADE
);

CREATE TRIGGER ValoracioMediaI
After Insert on valoracion For Each ROW

Begin

    declare mediaO double;
    declare numO double;
    declare new_media double;
    declare new_num double;
    declare suma double;

    SELECT  media into mediaO From publicacion where (new.idpubli = publicacion.id);
    SELECT numerototalval into numO From publicacion where (new.idpubli = publicacion.id);

    set suma :=(mediaO*numO)+new.punt;
    set new_num :=numO+1;
    set new_media :=suma/new_num;

    update  publicacion
    set media = new_media where new.idpubli=publicacion.id;
    update  publicacion
    set numerototalval = new_num where new.idpubli=publicacion.id;

end;

CREATE TRIGGER ValoracioMediaD
After delete on valoracion For Each ROW

Begin

    declare mediaO double;
    declare numO double;
    declare new_media double;
    declare new_num double;
    declare suma double;

    SELECT  media into mediaO From publicacion where (old.idpubli = publicacion.id);
    SELECT numerototalval into numO From publicacion where (old.idpubli = publicacion.id);

    set suma :=(mediaO*numO)-old.punt;
    set new_num :=numO-1;
    set new_media :=suma/new_num;

    update  publicacion
    set media = new_media where old.idpubli=publicacion.id;
    update  publicacion
    set numerototalval = new_num where old.idpubli=publicacion.id;

end;

CREATE TRIGGER ValoracioMediaU
After update on valoracion For Each ROW

Begin

    declare mediaO double;
    declare numO double;
    declare new_media double;
    declare suma double;

    SELECT  media into mediaO From publicacion where (old.idpubli = publicacion.id);
    SELECT numerototalval into numO From publicacion where (old.idpubli = publicacion.id);

    set suma :=(mediaO*numO)+(new.punt-old.punt);
    set new_media :=suma/numO;

    update  publicacion
    set media = new_media where old.idpubli=publicacion.id;

end;


CREATE EVENT borrarCosasExpiradas
  ON SCHEDULE EVERY 1 MINUTE
  DO
  BEGIN
    delete from verifytoken where NOW() > expiredate;
    delete from jwtoken where NOW() > expiredate;
    delete from usuario_baneado where NOW() > fecha;
    delete from refreshtoken where NOW() > expiredate;
END;
    
    
CREATE TRIGGER borrarusuariotoken
After delete on verifytoken For Each ROW

Begin

    delete from usuario where OLD.email = email AND enabled = false;

end;


create trigger updateOnUnsubscribe
before delete on usuario
for each row
begin 
	delete from amigo where (iduser1 = OLD.id) OR (iduser2 = OLD.id);
    delete from colaborador where id = OLD.id;
    delete from comentario where iduser = OLD.id;
    delete from meetup where iduser = OLD.id;
    delete from mensaje where (iduser1 = OLD.id) OR (iduser2 = OLD.id);
    delete from publicacion where iduser = OLD.id;
    delete from valoracion where iduser = OLD.id;
end;


create trigger insertNumValPubliUser 
after insert on usuario for each row
begin 
   insert into numvalpubli (iduser, numval, numpubli) values (new.id, 0, 0);
end;


create trigger insertNumVal
after insert on valoracion for each row
begin
    declare idu int;
    select publicacion.iduser into idu from publicacion where publicacion.id = new.idpubli;
    update numvalpubli
    set numval = numval+1 where idu = iduser;
end;


create trigger insertNumPubli
after insert on publicacion for each row
begin
    update numvalpubli
    set numpubli = numpubli+1 where new.iduser = iduser;
end;


create trigger deleteNumPubli
after delete on publicacion for each row
begin
    declare numeroVal int;
    update numvalpubli
    set numpubli = numpubli-1 where old.iduser = iduser;
    select count(*) into numeroVal from valoracion where idpubli = old.id;
    update numvalpubli
    set numval = numval-numVal where old.iduser = iduser;
end;


create trigger desbaneo
    after delete on usuario_baneado for each row

    begin
    update usuario
    set enabled=true where OLD.id=usuario.id;
end;

create trigger changeVIP
after delete on patrocinio for each row
begin
    update colaborador
    set vip = false where colaborador.id = old.id;
end;
