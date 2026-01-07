-- Insert default establishment if it doesn't exist
INSERT INTO establishment (id, name, description, address, phone, opening_time, closing_time)
SELECT 1, 'Casa Manteca',
       'Un icono culinario de Cádiz con más de 70 años de historia especializado en montaditos de atún, jamón ibérico y conservas gourmet.',
       'Cádiz, Andalucía, España',
       '+34 956 280 513',
       '10:00:00',
       '23:00:00'
WHERE NOT EXISTS (SELECT 1 FROM establishment WHERE id = 1);

