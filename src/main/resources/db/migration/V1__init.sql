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

--INSERT INTO books (isbn, title, author, pub_year, copies_total, copies_avail)
--VALUES
--    ('1-111', 'War and Peace', 'Leo Tolstoy', 1867, 10, 10),
--    ('1-112', 'Crime and Punishment', 'Theodor Dostoevski', 1866, 8, 8),
--    ('1-113', 'Red and White', 'Stendhal', 1830, 4, 4),
--    ('1-114', 'Chuk and Gek', 'Arkadiy Gaydar', 1939, 1, 1)
--    ON CONFLICT (title) DO NOTHING;

-- users
CREATE TABLE IF NOT EXISTS users (
  id BIGSERIAL PRIMARY KEY,
  full_name VARCHAR(200) NOT NULL,
  email VARCHAR(200) NOT NULL UNIQUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--INSERT INTO users (full_name, email)
--VALUES
--    ('Tom Hanks', 'hanks@mail.com'),
--    ('Tom Cruise', 'cruise@mail.net'),
--    ('Tom Hardy', 'hardy@gmail.org'),
--    ON CONFLICT (full_name) DO NOTHING;

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
