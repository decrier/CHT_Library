-- books
CREATE TABLE IF NOT EXISTS books (
  id BIGSERIAL PRIMARY KEY,
  isbn VARCHAR(50) NOT NULL UNIQUE,
  title VARCHAR(200) NOT NULL,
  author VARCHAR(200) NOT NULL,
  pub_year INT NOT NULL,
  copies_total INT NOT NULL,
  copies_avail INT NOT NULL
);

-- users
CREATE TABLE IF NOT EXISTS users (
  id BIGSERIAL PRIMARY KEY,
  full_name VARCHAR(200) NOT NULL,
  email VARCHAR(200) NOT NULL UNIQUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- loans
CREATE TABLE IF NOT EXISTS loans (
  id BIGSERIAL PRIMARY KEY,
  book_id BIGINT NOT NULL REFERENCES books(id) ON DELETE RESTRICT,
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
  loan_date DATE NOT NULL,
  due_date  DATE NOT NULL,
  return_date DATE
);

CREATE INDEX IF NOT EXISTS idx_loans_user ON loans(user_id);
CREATE INDEX IF NOT EXISTS idx_loans_book ON loans(book_id);
CREATE INDEX IF NOT EXISTS idx_loans_open ON loans(user_id, book_id, return_date);
