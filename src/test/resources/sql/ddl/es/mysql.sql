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
