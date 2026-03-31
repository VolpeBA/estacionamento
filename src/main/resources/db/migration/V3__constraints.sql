ALTER TABLE parking_sessions
    ADD CONSTRAINT chk_session_status CHECK (status IN ('PENDING_SPOT', 'ACTIVE', 'COMPLETED'));

ALTER TABLE sectors
    ADD CONSTRAINT chk_max_capacity CHECK (max_capacity > 0);

ALTER TABLE parking_sessions
    ADD CONSTRAINT chk_amount_charged CHECK (amount_charged IS NULL OR amount_charged >= 0);
