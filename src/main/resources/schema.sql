CREATE TABLE IF NOT EXISTS member (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    duration_months INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS tournament (
    id BIGSERIAL PRIMARY KEY,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    location VARCHAR(255) NOT NULL,
    entry_fee NUMERIC(10, 2) NOT NULL,
    cash_prize NUMERIC(15, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS tournament_participants (
    tournament_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    PRIMARY KEY (tournament_id, member_id),
    FOREIGN KEY (tournament_id) REFERENCES tournament(id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
);