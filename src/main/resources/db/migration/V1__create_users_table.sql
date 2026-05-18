CREATE TABLE users (
    id        NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name      VARCHAR2(255) NOT NULL,
    email     VARCHAR2(255) NOT NULL,
    direccion VARCHAR2(500) NOT NULL,
    CONSTRAINT uk_users_email UNIQUE (email)
);
