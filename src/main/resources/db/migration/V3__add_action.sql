SET search_path TO calendar;

CREATE TABLE action(
    id SERIAL NOT NULL PRIMARY KEY,
    name varchar NOT NULL UNIQUE
);

INSERT INTO action (name) VALUES
    ('meeting');