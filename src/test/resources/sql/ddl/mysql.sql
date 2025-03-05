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
    description VARCHAR(1024)
);

CREATE TABLE IF NOT EXISTS members (
    id VARCHAR(255) PRIMARY KEY,
    board_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    FOREIGN KEY (board_id) REFERENCES boards(id)
);

ALTER TABLE members ADD CONSTRAINT UNIQUE_BOARD_USER UNIQUE (board_id, user_id);

CREATE TABLE IF NOT EXISTS statuses (
    id VARCHAR(255) PRIMARY KEY,
    board_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1024),
    category VARCHAR(255) NOT NULL,
    FOREIGN KEY (board_id) REFERENCES boards(id)
);

CREATE TABLE IF NOT EXISTS components (
    id VARCHAR(255) PRIMARY KEY,
    board_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1024),
    FOREIGN KEY (board_id) REFERENCES boards(id)
);

CREATE TABLE IF NOT EXISTS board_columns (
    board_id VARCHAR(255) NOT NULL,
    status_id VARCHAR(255) NOT NULL,
    position INT NOT NULL,
    FOREIGN KEY (board_id) REFERENCES boards(id),
    FOREIGN KEY (status_id) REFERENCES statuses(id),
    PRIMARY KEY (board_id, status_id)
);

CREATE TABLE IF NOT EXISTS tasks (
    id VARCHAR(255) PRIMARY KEY,
    board_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1024),
    status_id VARCHAR(255),
    status_updated_at DATETIME,
    assignee_id VARCHAR(255),
    due_date DATETIME,
    created_at DATETIME NOT NULL,
    priority VARCHAR(255),
    estimated_effort BIGINT
);

CREATE TABLE IF NOT EXISTS task_components (
    task_id VARCHAR(255) NOT NULL,
    component_id VARCHAR(255) NOT NULL,
    FOREIGN KEY (task_id) REFERENCES tasks(id),
    PRIMARY KEY (task_id, component_id)
);
