-- Insert default establishment if it doesn't exist
INSERT INTO establishment (id, name, description, address, phone, opening_time, closing_time)
SELECT 1, 'Casa Manteca',
       'Un icono culinario de Cádiz con más de 70 años de historia especializado en montaditos de atún, jamón ibérico y conservas gourmet.',
       'Cádiz, Andalucía, España',
       '+34 956 280 513',
       '10:00:00',
       '23:00:00'
WHERE NOT EXISTS (SELECT 1 FROM establishment WHERE id = 1);

-- Insert ADMIN role if it doesn't exist
INSERT INTO role (name)
SELECT 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM role WHERE name = 'ADMIN');

-- Insert default admin user if it doesn't exist
-- Password: admin123 (BCrypt hashed)
INSERT INTO users (username, password, email, role_id)
SELECT 'admin',
       '$2a$10$WW.kQvMRvmjp8ECTZ8iZCeIqJW0LQOT3bQp.WDXXFqKc4OxmEbvQa',
       'admin@casamanteca.com',
       (SELECT id FROM role WHERE name = 'ADMIN')
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');
