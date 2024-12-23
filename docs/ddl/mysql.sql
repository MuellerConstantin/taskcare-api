CREATE TABLE IF NOT EXISTS es_metadata (
    aggregate_id VARCHAR(255) PRIMARY KEY,
    aggregate_type VARCHAR(1024) NOT NULL,
    version INT NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS es_events (
    aggregate_id VARCHAR(255) NOT NULL,
    version INT NOT NULL,
    event_type VARCHAR(1024) NOT NULL,
    event_data TEXT NOT NULL,
    PRIMARY KEY (aggregate_id, version),
    FOREIGN KEY (aggregate_id) REFERENCES es_metadata(aggregate_id)
);

CREATE TABLE IF NOT EXISTS es_snapshots (
    aggregate_id VARCHAR(255) NOT NULL,
    version INT NOT NULL,
    aggregate_data TEXT NOT NULL,
    PRIMARY KEY (aggregate_id, version),
    FOREIGN KEY (aggregate_id) REFERENCES es_metadata(aggregate_id)
);

CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(255) PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255),
    display_name VARCHAR(255),
    role VARCHAR(255) NOT NULL,
    identity_provider VARCHAR(255) NOT NULL,
    locked BOOLEAN NOT NULL DEFAULT FALSE
);

ALTER TABLE users ADD CONSTRAINT UNIQUE_USERNAME UNIQUE (username);

CREATE TABLE IF NOT EXISTS boards (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS members (
    id VARCHAR(255) PRIMARY KEY,
    board_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    FOREIGN KEY (board_id) REFERENCES boards(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

ALTER TABLE members ADD CONSTRAINT UNIQUE_BOARD_USER UNIQUE (board_id, user_id);
