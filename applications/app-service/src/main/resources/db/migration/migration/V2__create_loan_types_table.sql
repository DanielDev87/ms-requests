-- Creación de la tabla para los tipos de préstamo
CREATE TABLE loan_types (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    interest_rate DECIMAL(5, 2) NOT NULL,
    min_amount DECIMAL(15, 2),
    max_amount DECIMAL(15, 2)
);

-- Insertar algunos datos de ejemplo
INSERT INTO loan_types (name, interest_rate, min_amount, max_amount) VALUES
('Préstamo de Libre Inversión', 18.50, 1000000, 100000000),
('Préstamo para Vehículo', 14.75, 5000000, 200000000),
('Préstamo para Vivienda', 9.20, 20000000, 1000000000);