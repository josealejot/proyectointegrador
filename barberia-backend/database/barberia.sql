-- Base de datos del proyecto integrador: agenda de citas para barberia.
-- Spring Boot crea estas tablas automaticamente (ddl-auto=update);
-- este script sirve para crearlas a mano y como soporte del diagrama relacional.

CREATE DATABASE IF NOT EXISTS barberia;
USE barberia;

CREATE TABLE IF NOT EXISTS cliente (
    idcliente  VARCHAR(10)  NOT NULL,
    nomcliente VARCHAR(80)  NOT NULL,
    telcliente VARCHAR(10)  NOT NULL,
    correo     VARCHAR(100) NOT NULL,
    activo     BIT(1)       NOT NULL,
    PRIMARY KEY (idcliente)
);

CREATE TABLE IF NOT EXISTS barbero (
    idbarbero    VARCHAR(10) NOT NULL,
    nombarbero   VARCHAR(80) NOT NULL,
    telbarbero   VARCHAR(10) NOT NULL,
    especialidad VARCHAR(30) NOT NULL,
    activo       BIT(1)      NOT NULL,
    PRIMARY KEY (idbarbero)
);

CREATE TABLE IF NOT EXISTS servicio (
    codservicio INT         NOT NULL AUTO_INCREMENT,
    nomservicio VARCHAR(60) NOT NULL,
    precio      INT         NOT NULL,
    duracion    INT         NOT NULL,
    activo      BIT(1)      NOT NULL,
    PRIMARY KEY (codservicio),
    UNIQUE KEY uk_servicio_nombre (nomservicio)
);

CREATE TABLE IF NOT EXISTS usuario (
    idusuario  INT          NOT NULL AUTO_INCREMENT,
    nombre     VARCHAR(80)  NOT NULL,
    correo     VARCHAR(100) NOT NULL,
    contrasena VARCHAR(100) NOT NULL,
    rol        VARCHAR(20)  NOT NULL,
    activo     BIT(1)       NOT NULL,
    PRIMARY KEY (idusuario),
    UNIQUE KEY uk_usuario_correo (correo)
);

CREATE TABLE IF NOT EXISTS cita (
    codcita     INT          NOT NULL AUTO_INCREMENT,
    fecha       DATE         NOT NULL,
    hora        TIME         NOT NULL,
    idcliente   VARCHAR(10)  NOT NULL,
    idbarbero   VARCHAR(10)  NOT NULL,
    codservicio INT          NOT NULL,
    metodopago  VARCHAR(20)  NOT NULL,
    estado      VARCHAR(20)  NOT NULL,
    observacion VARCHAR(200) NOT NULL DEFAULT '',
    PRIMARY KEY (codcita),
    CONSTRAINT fk_cita_cliente  FOREIGN KEY (idcliente)   REFERENCES cliente (idcliente),
    CONSTRAINT fk_cita_barbero  FOREIGN KEY (idbarbero)   REFERENCES barbero (idbarbero),
    CONSTRAINT fk_cita_servicio FOREIGN KEY (codservicio) REFERENCES servicio (codservicio)
);
