-- Drop constraints first
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_system_role_check;
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;

-- Insert Communities
INSERT INTO community (created_at, updated_at, contact_email, contact_phone, description, establishment_year, is_active, logo_url, name, slug) VALUES 
(NOW(), NOW(), 'info@yazilim.com', '5551234567', 'Yazılım ve Teknoloji Topluluğu', 2024, true, null, 'Yazılım Topluluğu', 'yazilim'),
(NOW(), NOW(), 'info@robotik.com', '5551234568', 'Robotik ve Otomasyon Topluluğu', 2023, true, null, 'Robotik Topluluğu', 'robotik'),
(NOW(), NOW(), 'info@oyun.com', '5551234569', 'Oyun Geliştirme Topluluğu', 2025, true, null, 'Oyun Topluluğu', 'oyun');

-- We won't insert users directly into SQL because of password hashing, but we can if we copy a known hash.
-- Actually, the user has an existing user 'aydogannn' with a hashed password, which we saw in the error log!
-- Let's just insert some more communities so the user has data to look at on the Communities page.
