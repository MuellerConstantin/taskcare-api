CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(255) PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(255),
    role VARCHAR(255) NOT NULL,
    identity_provider VARCHAR(255) NOT NULL,
    locked BOOLEAN NOT NULL DEFAULT FALSE
);
