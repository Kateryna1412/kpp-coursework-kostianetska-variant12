DROP DATABASE IF EXISTS equipment_rental;
CREATE DATABASE equipment_rental CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE equipment_rental;
CREATE TABLE equipment (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(160) NOT NULL,
                           category VARCHAR(100) NOT NULL,
                           rental_rate DOUBLE NOT NULL
);
CREATE TABLE client (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        full_name VARCHAR(160) NOT NULL,
                        phone VARCHAR(30) NOT NULL,
                        email VARCHAR(100) NOT NULL
);
CREATE TABLE rental_request (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                client_id BIGINT NOT NULL,
                                equipment_id BIGINT NOT NULL,
                                state VARCHAR(100) NOT NULL,
                                f1 DOUBLE NOT NULL,
                                f2 DOUBLE NOT NULL,
                                f3 DOUBLE NOT NULL,
                                integral_index DOUBLE NOT NULL,
                                FOREIGN KEY (client_id) REFERENCES client(id),
                                FOREIGN KEY (equipment_id) REFERENCES equipment(id)
);
CREATE TABLE rental_contract (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 request_id BIGINT NOT NULL,
                                 contract_date VARCHAR(20) NOT NULL,
                                 total_cost DOUBLE NOT NULL,
                                 FOREIGN KEY (request_id) REFERENCES rental_request(id)
);
INSERT INTO equipment (name, category, rental_rate) VALUES
                                                        ('Перфоратор Makita', 'Електроінструмент', 280.0),
                                                        ('Бетонозмішувач Dnipro-M', 'Будівельне обладнання', 450.0);

INSERT INTO client (full_name, phone, email) VALUES
                                                 ('Іваненко Петро Семенович', '+380971234567', 'ivanenko@example.com'),
                                                 ('Петренко Ганна Василівна', '+380639876543', 'petrenko@example.com');

INSERT INTO rental_request (client_id, equipment_id, state, f1, f2, f3, integral_index) VALUES
                                                                                            (1, 1, 'CREATED', 40.0, 50.0, 30.0, 41.0),
                                                                                            (2, 2, 'CONFIRMED', 20.0, 40.0, 60.0, 37.0);

INSERT INTO rental_contract (request_id, contract_date, total_cost) VALUES
    (2, '2026-11-01', 1350.0);
