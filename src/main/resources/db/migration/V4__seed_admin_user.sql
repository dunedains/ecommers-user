-- Usuario administrador inicial (bootstrap).
-- Sin este seed es imposible crear un ADMIN: el endpoint POST /api/v1/users
-- exige rol ADMIN en el gateway y /auth/register solo crea usuarios USER.
-- Credenciales por defecto: admin@ecommers.cl / Admin123!  (cambiar en produccion)
INSERT INTO users (name, email, address, password, role)
VALUES (
    'Administrador',
    'admin@ecommers.cl',
    'Oficina Central',
    '$2y$10$0MONeRgmj/sd/Z1Dfi7Ld.UJ6FFRhEBmkx10qnY9f3poegqDV2tyC',
    'ADMIN'
)
ON CONFLICT ON CONSTRAINT uk_users_email DO NOTHING;
