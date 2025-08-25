DROP DATABASE IF EXISTS biblioteca;
CREATE DATABASE biblioteca;
USE biblioteca ;

CREATE TABLE usuario(
    idUsuario INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombreUsuario VARCHAR(20) NOT NULL,
    contrasenia varchar(20) not null,
    estatus INT NOT NULL DEFAULT 1,    -- 1 activo, 0 inactivo
    rol VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL
);

create table libros(
	id_libro int not null auto_increment primary key,
    nombre_libro varchar(100) not null,
    autor varchar(100) not null,
    genero VARCHAR(50) not null,
    estatus INT NOT NULL DEFAULT 1, -- 1 activo 0 inactivo
    archivo_pdf MEDIUMBLOB NOT NULL,
    universidad varchar(100) not null DEFAULT 'Biblioteca 1'
);

ALTER DATABASE biblioteca CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE libros CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- -----------------------------------------------------------------------------------------------------------------------------------
-- -----------------------------------------------------------------------------------------------------------------------------------
insert into usuario (nombreUsuario, contrasenia, estatus, rol, email) values
("Alumno", "Alumno", 1, "Alumno", "Alumno@Alumno.com"),
("Bibliotecario", "Bibliotecario", 1, "Bibliotecario", "Bibliotecario@Bibliotecario.com"),
("Administrador", "Administrador", 1, "Administrador", "admin@admin.com");

-- ---------------------------------------------------------------------------------------------------------------------------------------
-- ------------------------------------------------------------------------------------------------------------------------------------------
CREATE VIEW vista_usuario_activo AS
SELECT 
    idUsuario,
    nombreUsuario,
    contrasenia,
    rol,
    email
FROM 
    usuario
WHERE 
    estatus = 1;
 -- --------------------------------------------------------- ---------------------------------------------------------------------------------  
CREATE VIEW vista_libros AS
SELECT 
    id_libro,
    nombre_libro,
    autor,
    genero,
    estatus,
    archivo_pdf,
    universidad
FROM 
    libros;
-- ----------------------------------------------------------------------------------------------------------------------------------------
CREATE VIEW vista_usuario AS
SELECT 
    idUsuario,
    nombreUsuario,
    contrasenia,
    estatus,
    rol,
    email
FROM 
    usuario;
-- ----------------------------------------------------------------------------------------------------------------------
-- ----------------------------------------------------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS insertarUsuario;
DELIMITER $$

CREATE PROCEDURE insertarUsuario(
    IN var_nombreUsuario VARCHAR(20),
    IN var_contrasenia VARCHAR(20),
    IN var_estatus INT,
    IN var_rol VARCHAR(20),
    IN var_email VARCHAR(100),
    OUT var_idUsuario INT
)
BEGIN
    -- Validar que los parametros no sean NULL
    IF var_nombreUsuario IS NULL OR TRIM(var_nombreUsuario) = '' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'El nombre de usuario no puede ser nulo';
    END IF;

    IF var_contrasenia IS NULL OR TRIM(var_contrasenia) = '' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'La contraseña no puede ser nula';
    END IF;

    IF var_rol IS NULL OR TRIM(var_rol) = '' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'El rol no puede ser nulo';
    END IF;

    IF var_email IS NULL OR TRIM(var_email) = '' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'El email no puede ser nulo';
    END IF;

    IF var_estatus IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'El estatus no puede ser nulo';
    END IF;

    INSERT INTO usuario (
        nombreUsuario,
        contrasenia,
        estatus,
        rol,
        email
    ) VALUES (
        var_nombreUsuario,
        var_contrasenia,
        var_estatus,
        var_rol,
        var_email
    );

    SET var_idUsuario = LAST_INSERT_ID();
END$$

DELIMITER ;
-- -----------------------------------------------------------------------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS modificarUsuario;

DELIMITER $$

CREATE PROCEDURE modificarUsuario(
    IN var_idUsuario INT,
    IN var_nombreUsuario VARCHAR(20),
    IN var_contrasenia VARCHAR(20),
    IN var_estatus INT,
    IN var_rol VARCHAR(20),
    IN var_email VARCHAR(100)
)
BEGIN
    -- Validar que los parametros de entrada no sean NULL
    IF var_idUsuario IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El ID de usuario es requerido';
    END IF;

    IF var_nombreUsuario IS NULL OR TRIM(var_nombreUsuario) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El nombre de usuario es requerido';
    END IF;

    IF var_contrasenia IS NULL OR TRIM(var_contrasenia) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La contraseña es requerida';
    END IF;

    IF var_estatus IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El estatus es requerido';
    END IF;

    IF var_rol IS NULL OR TRIM(var_rol) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El rol es requerido';
    END IF;

    IF var_email IS NULL OR TRIM(var_email) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El email es requerido';
    END IF;

    UPDATE usuario
    SET 
        nombreUsuario = var_nombreUsuario,
        contrasenia = var_contrasenia,
        estatus = var_estatus,
        rol = var_rol,
        email = var_email
    WHERE idUsuario = var_idUsuario;
END$$

DELIMITER ;
-- ----------------------------------------------------------------------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS insertarLibro;
DELIMITER $$
CREATE PROCEDURE insertarLibro(
    IN var_nombre_libro VARCHAR(100), 
    IN var_autor VARCHAR(100),   
    IN var_genero VARCHAR(50),   
    IN var_estatus INT,   
    IN var_archivo_pdf MEDIUMBLOB,
    OUT var_id_libro INT
)
BEGIN
    -- Validaciones
    IF var_nombre_libro IS NULL OR TRIM(var_nombre_libro) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El nombre del libro es requerido';
    END IF;

    IF var_autor IS NULL OR TRIM(var_autor) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El autor es requerido';
    END IF;

    IF var_genero IS NULL OR TRIM(var_genero) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El genero es requerido';
    END IF;

    IF var_estatus IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El estatus es requerido';
    END IF;

    IF var_archivo_pdf IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El archivo PDF es requerido';
    END IF;

    INSERT INTO libros (nombre_libro, autor, genero, estatus, archivo_pdf)
    VALUES (var_nombre_libro, var_autor, var_genero, var_estatus, var_archivo_pdf);
    
    SET var_id_libro = LAST_INSERT_ID();
END
$$
DELIMITER ;
-- -----------------------------------------------------------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS modificarLibro;
DELIMITER $$
CREATE PROCEDURE modificarLibro(
    IN var_id_libro INT, 
    IN var_nombre_libro VARCHAR(100),
    IN var_autor VARCHAR(100),
    IN var_genero VARCHAR(50),
    IN var_estatus INT,
    IN var_archivo_pdf MEDIUMBLOB
)
BEGIN
    -- Validación básica
    IF var_id_libro IS NULL OR var_id_libro <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El ID del libro es inválido';
    END IF;

    -- Actualizar los datos del libro en la tabla libros
    UPDATE libros
    SET 
        nombre_libro = var_nombre_libro,
        autor = var_autor,
        genero = var_genero,
        estatus = var_estatus,
        archivo_pdf = var_archivo_pdf
    WHERE id_libro = var_id_libro;
END
$$

DELIMITER ;
-- ------------------------------------------------------------------------------------------------------------------------------------------
-- ----------------------------------------------------------------------------------------------------------------------------------
select * from vista_usuario_activo;
select * from vista_libros;
select * from vista_usuario;

select * from libros;
select * from usuario;