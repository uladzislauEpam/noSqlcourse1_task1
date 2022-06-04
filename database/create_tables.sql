CREATE TABLE events
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY,
    title VARCHAR(50) NOT NULL,
    date  DATE        NOT NULL
);

CREATE TABLE users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY,
    name  VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL
);

CREATE TABLE tickets
(
    id       BIGINT GENERATED ALWAYS AS IDENTITY,
    user_id  BIGINT,
    event_id BIGINT,
    place    INT           NOT NULL,
    category VARCHAR(50)   NOT NULL,
    price    DECIMAL(6, 2) NOT NULL
);

CREATE TABLE user_accounts
(
    id      BIGINT GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT,
    money   DECIMAL(6, 2) NOT NULL
);