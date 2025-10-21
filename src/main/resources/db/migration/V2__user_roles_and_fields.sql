-- роли: reader | librarian
ALTER TABLE users
    ADD COLUMN role TEXT NOT NULL DEFAULT 'reader'
    CHECK ( role IN ('reader', 'librarian') );

ALTER TABLE users
    ADD COLUMN membership_date DATE NOT NULL DEFAULT CURRENT_DATE;

ALTER TABLE users
    ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;

-- индексы под типичные фильтры
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_users_active ON users(active);
CREATE INDEX IF NOT EXISTS idx_users_membership_date ON users(membership_date);