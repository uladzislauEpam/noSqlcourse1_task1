ALTER TABLE events
    ADD CONSTRAINT pk_events PRIMARY KEY (id);
ALTER TABLE events
    ADD CONSTRAINT uq_events_title_date UNIQUE (title, date);

ALTER TABLE users
    ADD CONSTRAINT pk_users PRIMARY KEY (id);
ALTER TABLE users
    ADD CONSTRAINT uq_users_email UNIQUE (email);

ALTER TABLE tickets
    ADD CONSTRAINT pk_tickets PRIMARY KEY (id);
ALTER TABLE tickets
    ADD CONSTRAINT fk_tickets_users FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE tickets
    ADD CONSTRAINT fk_tickets_events FOREIGN KEY (event_id) REFERENCES events (id);
ALTER TABLE tickets
    ADD CONSTRAINT uq_tickets_event_id_place UNIQUE (event_id, place);
ALTER TABLE tickets
    ADD CONSTRAINT uq_tickets_user_id_event_id_place UNIQUE (user_id, event_id, place);

ALTER TABLE user_accounts
    ADD CONSTRAINT pk_user_accounts PRIMARY KEY (id);
ALTER TABLE user_accounts
    ADD CONSTRAINT fk_user_accounts_users FOREIGN KEY (user_id) REFERENCES users (id);
