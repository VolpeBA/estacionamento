CREATE INDEX idx_sessions_license_plate ON parking_sessions (license_plate);
CREATE INDEX idx_sessions_status ON parking_sessions (status);
CREATE INDEX idx_sessions_sector_exit ON parking_sessions (sector_id, exit_time);
CREATE INDEX idx_spots_sector ON parking_spots (sector_id);
CREATE INDEX idx_spots_lat_lng ON parking_spots (lat, lng);
