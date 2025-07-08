-- First insert user roles (no problem here)
INSERT INTO user_roles (id, name) VALUES
    (0, 'SUPER_ADMIN'),
    (1, 'ADMIN'),
    (2, 'USER');

-- Insert SUPER_ADMIN (approved_by is NULL as it's the first user)
INSERT INTO users (
    id, name, user_name, mobile_number, email, user_type, profile_image_url,
    email_verified, phone_verified, approval_status,
    is_active, is_deleted, is_logged_in, approved_by, adhaar_number,
    created_at, updated_at, last_login_at, password, has_password
) VALUES (
    '1b92d27b-4af1-4f2f-a650-123456789abc', 'Manoj Kumar', 'manoj_admin', '9876543210', 'manoj@example.com', 0,
    NULL, TRUE, TRUE, TRUE,
    TRUE, FALSE, TRUE, NULL, '123456789012',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'hashed_password_001', FALSE
);

-- Insert another USER, approved by SUPER_ADMIN
INSERT INTO users (
    id, name, user_name, mobile_number, email, user_type, profile_image_url,
    email_verified, phone_verified, approval_status,
    is_active, is_deleted, is_logged_in, approved_by, adhaar_number,
    created_at, updated_at, last_login_at, password, has_password
) VALUES (
    '7fd9ac10-4b31-4907-8411-abcdefabcdef', 'Ravi Kumar', 'ravi_user', '9123456789', 'ravi@example.com', 2,
    NULL, TRUE, TRUE, TRUE,
    TRUE, FALSE, FALSE, '1b92d27b-4af1-4f2f-a650-123456789abc', '987654321098',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'hashed_password_002', FALSE
);
