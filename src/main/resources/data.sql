-- Creación del Schema (Base de Datos) si no existe
CREATE DATABASE IF NOT EXISTS `biblioteca` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `biblioteca`;

-- Tabla: roles_usuario
CREATE TABLE IF NOT EXISTS `biblioteca`.`roles_usuario` (
                                                            `id_rol_usuario` BIGINT NOT NULL AUTO_INCREMENT,
                                                            `nombre_rol` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`id_rol_usuario`),
    UNIQUE INDEX `UK_nombre_rol` (`nombre_rol` ASC) VISIBLE
    ) ENGINE = InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabla: editoriales
CREATE TABLE IF NOT EXISTS `biblioteca`.`editoriales` (
                                                          `id_editorial` BIGINT NOT NULL AUTO_INCREMENT,
                                                          `nombre_editorial` VARCHAR(150) NOT NULL,
    PRIMARY KEY (`id_editorial`),
    UNIQUE INDEX `UK_nombre_editorial` (`nombre_editorial` ASC) VISIBLE
    ) ENGINE = InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabla: idiomas
CREATE TABLE IF NOT EXISTS `biblioteca`.`idiomas` (
                                                      `id_idioma` BIGINT NOT NULL AUTO_INCREMENT,
                                                      `nombre_idioma` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`id_idioma`),
    UNIQUE INDEX `UK_nombre_idioma` (`nombre_idioma` ASC) VISIBLE
    ) ENGINE = InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabla: generos
CREATE TABLE IF NOT EXISTS `biblioteca`.`generos` (
                                                      `id_genero` BIGINT NOT NULL AUTO_INCREMENT,
                                                      `nombre_genero` VARCHAR(100) NOT NULL,
    PRIMARY KEY (`id_genero`),
    UNIQUE INDEX `UK_nombre_genero` (`nombre_genero` ASC) VISIBLE
    ) ENGINE = InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabla: usuarios
CREATE TABLE IF NOT EXISTS `biblioteca`.`usuarios` (
                                                       `id_usuario` BIGINT NOT NULL AUTO_INCREMENT,
                                                       `nombre` VARCHAR(100) NOT NULL,
    `apellido` VARCHAR(100) NOT NULL,
    `dni` VARCHAR(20) NOT NULL,
    `fecha_nacimiento` DATE NULL,
    `direccion` VARCHAR(255) NULL,
    `correo_electronico` VARCHAR(150) NOT NULL,
    `id_rol_usuario` BIGINT NOT NULL,
    `activo` BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (`id_usuario`),
    UNIQUE INDEX `UK_dni_usuario` (`dni` ASC) VISIBLE,
    UNIQUE INDEX `UK_correo_electronico_usuario` (`correo_electronico` ASC) VISIBLE,
    INDEX `FK_usuario_rol_idx` (`id_rol_usuario` ASC) VISIBLE,
    CONSTRAINT `FK_usuario_rol`
    FOREIGN KEY (`id_rol_usuario`)
    REFERENCES `biblioteca`.`roles_usuario` (`id_rol_usuario`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
    ) ENGINE = InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabla: libros
CREATE TABLE IF NOT EXISTS `biblioteca`.`libros` (
                                                     `id_libro` BIGINT NOT NULL AUTO_INCREMENT,
                                                     `titulo` VARCHAR(255) NOT NULL,
    `autor` VARCHAR(255) NULL,
    `descripcion` TEXT NULL,
    `fecha_publicacion` DATE NULL,
    `id_editorial` BIGINT NOT NULL,
    `id_idioma` BIGINT NOT NULL,
    `isbn` VARCHAR(20) NULL,
    `cantidad_ejemplares` INT NOT NULL,
    `ruta_pdf` VARCHAR(500) NULL,
    `activo` BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (`id_libro`),
    UNIQUE INDEX `UK_isbn_libro` (`isbn` ASC) VISIBLE,
    INDEX `FK_libro_editorial_idx` (`id_editorial` ASC) VISIBLE,
    INDEX `FK_libro_idioma_idx` (`id_idioma` ASC) VISIBLE,
    CONSTRAINT `FK_libro_editorial`
    FOREIGN KEY (`id_editorial`)
    REFERENCES `biblioteca`.`editoriales` (`id_editorial`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
    CONSTRAINT `FK_libro_idioma`
    FOREIGN KEY (`id_idioma`)
    REFERENCES `biblioteca`.`idiomas` (`id_idioma`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
    ) ENGINE = InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabla de Unión: libro_genero
CREATE TABLE IF NOT EXISTS `biblioteca`.`libro_genero` (
                                                           `id_libro` BIGINT NOT NULL,
                                                           `id_genero` BIGINT NOT NULL,
                                                           PRIMARY KEY (`id_libro`, `id_genero`),
    INDEX `FK_librogenero_genero_idx` (`id_genero` ASC) VISIBLE,
    CONSTRAINT `FK_librogenero_libro`
    FOREIGN KEY (`id_libro`)
    REFERENCES `biblioteca`.`libros` (`id_libro`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    CONSTRAINT `FK_librogenero_genero`
    FOREIGN KEY (`id_genero`)
    REFERENCES `biblioteca`.`generos` (`id_genero`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    ) ENGINE = InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabla: prestamos
CREATE TABLE IF NOT EXISTS `biblioteca`.`prestamos` (
                                                        `id_prestamo` BIGINT NOT NULL AUTO_INCREMENT,
                                                        `id_libro` BIGINT NOT NULL,
                                                        `id_usuario` BIGINT NOT NULL,
                                                        `fecha_prestamo` DATETIME NOT NULL,
                                                        `fecha_devolucion_esperada` DATE NOT NULL,
                                                        `fecha_devolucion_real` DATE NULL,
                                                        `estado_prestamo` VARCHAR(20) NOT NULL,
    PRIMARY KEY (`id_prestamo`),
    INDEX `FK_prestamo_libro_idx` (`id_libro` ASC) VISIBLE,
    INDEX `FK_prestamo_usuario_idx` (`id_usuario` ASC) VISIBLE,
    CONSTRAINT `FK_prestamo_libro`
    FOREIGN KEY (`id_libro`)
    REFERENCES `biblioteca`.`libros` (`id_libro`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
    CONSTRAINT `FK_prestamo_usuario`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `biblioteca`.`usuarios` (`id_usuario`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
    ) ENGINE = InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Deshabilitar temporalmente las revisiones de claves foráneas para la carga de datos
SET FOREIGN_KEY_CHECKS=0;

-- Limpiar datos existentes en el orden correcto (hijos primero, o no importa tanto con FOREIGN_KEY_CHECKS=0)
DELETE FROM `biblioteca`.`prestamos`;
DELETE FROM `biblioteca`.`libro_genero`;
-- Los libros y usuarios pueden tener dependencias de prestamos y libro_genero, por eso se borran antes.
DELETE FROM `biblioteca`.`libros`;
DELETE FROM `biblioteca`.`usuarios`;
-- Tablas base
DELETE FROM `biblioteca`.`roles_usuario`;
DELETE FROM `biblioteca`.`generos`;
DELETE FROM `biblioteca`.`idiomas`;
DELETE FROM `biblioteca`.`editoriales`;

-- Resetear AUTO_INCREMENT para que los IDs comiencen desde 1 en cada ejecución
ALTER TABLE `biblioteca`.`roles_usuario` AUTO_INCREMENT = 1;
ALTER TABLE `biblioteca`.`editoriales` AUTO_INCREMENT = 1;
ALTER TABLE `biblioteca`.`idiomas` AUTO_INCREMENT = 1;
ALTER TABLE `biblioteca`.`generos` AUTO_INCREMENT = 1;
ALTER TABLE `biblioteca`.`usuarios` AUTO_INCREMENT = 1;
ALTER TABLE `biblioteca`.`libros` AUTO_INCREMENT = 1;
ALTER TABLE `biblioteca`.`prestamos` AUTO_INCREMENT = 1;


-- Carga de Datos (Orden corregido para dependencias)

-- 1. Tablas sin dependencias externas (o con dependencias ya satisfechas)
INSERT INTO `biblioteca`.`roles_usuario` (`id_rol_usuario`, `nombre_rol`) VALUES (1, 'ESTUDIANTE');
INSERT INTO `biblioteca`.`roles_usuario` (`id_rol_usuario`, `nombre_rol`) VALUES (2, 'PROFESOR');
INSERT INTO `biblioteca`.`roles_usuario` (`id_rol_usuario`, `nombre_rol`) VALUES (3, 'PUBLICO_GENERAL');
INSERT INTO `biblioteca`.`roles_usuario` (`id_rol_usuario`, `nombre_rol`) VALUES (4, 'ADMINISTRADOR');
COMMIT;

INSERT INTO `biblioteca`.`editoriales` (`id_editorial`, `nombre_editorial`) VALUES (1, 'Editorial Alpha');
INSERT INTO `biblioteca`.`editoriales` (`id_editorial`, `nombre_editorial`) VALUES (2, 'Ediciones Beta');
INSERT INTO `biblioteca`.`editoriales` (`id_editorial`, `nombre_editorial`) VALUES (3, 'Libros Gamma');
INSERT INTO `biblioteca`.`editoriales` (`id_editorial`, `nombre_editorial`) VALUES (4, 'Sudamericana');
INSERT INTO `biblioteca`.`editoriales` (`id_editorial`, `nombre_editorial`) VALUES (5, 'Minotauro');
COMMIT;

INSERT INTO `biblioteca`.`idiomas` (`id_idioma`, `nombre_idioma`) VALUES (1, 'Español');
INSERT INTO `biblioteca`.`idiomas` (`id_idioma`, `nombre_idioma`) VALUES (2, 'Inglés');
INSERT INTO `biblioteca`.`idiomas` (`id_idioma`, `nombre_idioma`) VALUES (3, 'Francés');
COMMIT;

INSERT INTO `biblioteca`.`generos` (`id_genero`, `nombre_genero`) VALUES (1, 'Ciencia Ficción');
INSERT INTO `biblioteca`.`generos` (`id_genero`, `nombre_genero`) VALUES (2, 'Fantasía');
INSERT INTO `biblioteca`.`generos` (`id_genero`, `nombre_genero`) VALUES (3, 'Misterio');
INSERT INTO `biblioteca`.`generos` (`id_genero`, `nombre_genero`) VALUES (4, 'Novela Histórica');
INSERT INTO `biblioteca`.`generos` (`id_genero`, `nombre_genero`) VALUES (5, 'Ensayo');
INSERT INTO `biblioteca`.`generos` (`id_genero`, `nombre_genero`) VALUES (6, 'Realismo Mágico');
COMMIT;

-- 2. Tablas que dependen de las anteriores
INSERT INTO `biblioteca`.`usuarios` (`id_usuario`, `nombre`, `apellido`, `dni`, `fecha_nacimiento`, `direccion`, `correo_electronico`, `id_rol_usuario`, `activo`)
VALUES
    (1, 'Juan', 'Pérez', '12345678A', '1990-05-15', 'Calle Falsa 123, Springfield', 'juan.perez@example.com', 1, true),
    (2, 'Ana', 'Gómez', '87654321B', '1985-11-20', 'Avenida Siempreviva 742, Buenos Aires', 'ana.gomez@example.com', 2, true),
    (3, 'Carlos', 'Ruiz', '11223344C', '2000-01-30', 'Plaza Mayor 1, Madrid', 'carlos.ruiz@example.com', 3, true),
    (4, 'Laura', 'Fernández', '22334455D', '1995-03-10', 'Boulevard de los Sueños Rotos 4', 'laura.f@example.com', 1, true);
COMMIT;

INSERT INTO `biblioteca`.`libros` (`id_libro`, `titulo`, `autor`, `descripcion`, `fecha_publicacion`, `id_editorial`, `id_idioma`, `isbn`, `cantidad_ejemplares`, `activo`, `ruta_pdf`)
VALUES
    (1, 'Duna', 'Frank Herbert', 'Épica novela de ciencia ficción ambientada en el desértico planeta Arrakis, centro del comercio de la melange.', '1965-08-01', 1, 2, '978-0441172719', 5, true, null),
    (2, 'El Señor de los Anillos: La Comunidad del Anillo', 'J.R.R. Tolkien', 'La primera parte de la épica trilogía de fantasía, donde Frodo Bolsón hereda el Anillo Único.', '1954-07-29', 5, 2, '978-0618640157', 3, true, 'libro_lotr_ejemplo.pdf'),
    (3, 'Cien Años de Soledad', 'Gabriel García Márquez', 'La historia multigeneracional de la familia Buendía en el mítico pueblo de Macondo.', '1967-05-30', 4, 1, '978-0307350438', 7, true, null),
    (4, 'Fundación', 'Isaac Asimov', 'El inicio de la monumental saga sobre la caída de un imperio galáctico y el plan para preservar el conocimiento.', '1951-06-01', 1, 2, '978-0553293357', 4, true, null),
    (5, 'El Aleph', 'Jorge Luis Borges', 'Colección de cuentos que exploran temas como el infinito, los laberintos y la identidad.', '1949-01-01', 4, 1, '978-9500404591', 6, true, null);
COMMIT;

-- 3. Tablas de Unión
INSERT INTO `biblioteca`.`libro_genero` (`id_libro`, `id_genero`) VALUES (1, 1);
INSERT INTO `biblioteca`.`libro_genero` (`id_libro`, `id_genero`) VALUES (1, 2);
INSERT INTO `biblioteca`.`libro_genero` (`id_libro`, `id_genero`) VALUES (2, 2);
INSERT INTO `biblioteca`.`libro_genero` (`id_libro`, `id_genero`) VALUES (3, 6);
INSERT INTO `biblioteca`.`libro_genero` (`id_libro`, `id_genero`) VALUES (4, 1);
INSERT INTO `biblioteca`.`libro_genero` (`id_libro`, `id_genero`) VALUES (5, 2);
INSERT INTO `biblioteca`.`libro_genero` (`id_libro`, `id_genero`) VALUES (5, 5);
COMMIT;

-- 4. Tabla de Préstamos (depende de libros y usuarios)
INSERT INTO `biblioteca`.`prestamos` (`id_prestamo`, `id_libro`, `id_usuario`, `fecha_prestamo`, `fecha_devolucion_esperada`, `estado_prestamo`)
VALUES (1, 1, 1, '2024-05-01 10:00:00', '2024-05-15', 'PRESTADO');
INSERT INTO `biblioteca`.`prestamos` (`id_prestamo`, `id_libro`, `id_usuario`, `fecha_prestamo`, `fecha_devolucion_esperada`, `fecha_devolucion_real`, `estado_prestamo`)
VALUES (2, 2, 2, '2024-04-20 14:30:00', '2024-05-04', '2024-05-02', 'DEVUELTO');
INSERT INTO `biblioteca`.`prestamos` (`id_prestamo`, `id_libro`, `id_usuario`, `fecha_prestamo`, `fecha_devolucion_esperada`, `estado_prestamo`)
VALUES (3, 3, 3, '2024-03-01 09:00:00', '2024-03-15', 'VENCIDO');
INSERT INTO `biblioteca`.`prestamos` (`id_prestamo`, `id_libro`, `id_usuario`, `fecha_prestamo`, `fecha_devolucion_esperada`, `estado_prestamo`)
VALUES (4, 4, 1, '2024-05-10 11:00:00', '2024-05-24', 'PRESTADO');
COMMIT;

-- Habilitar las revisiones de claves foráneas nuevamente
SET FOREIGN_KEY_CHECKS=1;
