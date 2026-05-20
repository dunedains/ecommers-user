CREATE TABLE users (
    id      BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name    VARCHAR(255) NOT NULL,
    email   VARCHAR(255) NOT NULL,
    address VARCHAR(500) NOT NULL,
    CONSTRAINT uk_users_email UNIQUE (email)
);
