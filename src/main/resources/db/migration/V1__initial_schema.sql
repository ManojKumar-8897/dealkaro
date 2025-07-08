-- 1. User Roles
CREATE TABLE IF NOT EXISTS user_roles (
    id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);






-- 2. Initial Setup Config
-- CREATE TABLE initial_setup_config (
--     id SERIAL PRIMARY KEY,
--     client_id VARCHAR(100) NOT NULL,
--     client_name VARCHAR(255) NOT NULL,
--     gstin VARCHAR(20),
--     licence_expiry DATE,
--     allowed_user_count INT DEFAULT 0,
--     is_initialized BOOLEAN DEFAULT FALSE,
--     is_active BOOLEAN DEFAULT TRUE,
--     setup_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- );

-- 3. Users
CREATE TABLE IF NOT EXISTS  users (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    user_name VARCHAR(100) NOT NULL UNIQUE,
    mobile_number VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(100) UNIQUE,
    user_type INT NOT NULL,
    profile_image_url VARCHAR(100),
    email_verified BOOLEAN DEFAULT FALSE,
    phone_verified BOOLEAN DEFAULT FALSE,
    approval_status BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    is_deleted BOOLEAN DEFAULT FALSE,
    is_logged_in BOOLEAN DEFAULT FALSE,
    approved_by UUID,
    adhaar_number VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP,
    password VARCHAR(100),
    has_password  BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_user_type FOREIGN KEY (user_type) REFERENCES user_roles(id),
    CONSTRAINT fk_approved_by FOREIGN KEY (approved_by) REFERENCES users(id)
);

--CREATE TABLE IF NOT EXISTS device_sessions (
--    id SERIAL PRIMARY KEY,
--    user_id VARCHAR(255) NOT NULL,  -- <-- Fix here
--    device_id VARCHAR(255) NOT NULL,
--    device_name VARCHAR(255),
--    ip_address VARCHAR(255),
--    user_agent TEXT,
--    login_time TIMESTAMP NOT NULL,
--    logout_time TIMESTAMP,
--    CONSTRAINT fk_device_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
--    CONSTRAINT uq_user_device UNIQUE (user_id, device_id)
--);
--
--
--
--
--CREATE TABLE IF NOT EXISTS refresh_tokens (
--    id SERIAL PRIMARY KEY,
--    token VARCHAR(255) NOT NULL UNIQUE,
--    expiry_date TIMESTAMP NOT NULL,
--    session_id BIGINT NOT NULL,
--    CONSTRAINT fk_token_session FOREIGN KEY (session_id) REFERENCES device_sessions(id) ON DELETE CASCADE
--);



CREATE TABLE device_sessions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    device_id UUID NOT NULL,
    device_name VARCHAR(100),
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_accessed TIMESTAMP,
    login_time TIMESTAMP,
    logout_time TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    token VARCHAR(100) UNIQUE NOT NULL,
    device_session_id UUID NOT NULL REFERENCES device_sessions(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE

);