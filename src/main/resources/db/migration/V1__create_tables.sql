CREATE TABLE sectors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sector_name VARCHAR(10) NOT NULL UNIQUE,
    base_price DECIMAL(10, 2) NOT NULL,
    max_capacity INT NOT NULL,
    open_hour VARCHAR(5),
    close_hour VARCHAR(5),
    duration_limit_minutes INT
);

CREATE TABLE parking_spots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sector_id BIGINT NOT NULL,
    lat DOUBLE NOT NULL,
    lng DOUBLE NOT NULL,
    occupied BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_spot_sector FOREIGN KEY (sector_id) REFERENCES sectors (id)
);

CREATE TABLE parking_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    license_plate VARCHAR(20) NOT NULL,
    sector_id BIGINT,
    spot_id BIGINT,
    entry_time DATETIME(3) NOT NULL,
    exit_time DATETIME(3),
    price_applied DECIMAL(10, 2),
    amount_charged DECIMAL(10, 2),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING_SPOT',
    CONSTRAINT fk_session_sector FOREIGN KEY (sector_id) REFERENCES sectors (id),
    CONSTRAINT fk_session_spot FOREIGN KEY (spot_id) REFERENCES parking_spots (id)
);

CREATE TABLE event_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_key VARCHAR(255) NOT NULL UNIQUE,
    event_type VARCHAR(20) NOT NULL,
    license_plate VARCHAR(20) NOT NULL,
    processed_at DATETIME(3) NOT NULL
);
