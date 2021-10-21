SET search_path TO calendar;

CREATE TABLE calendar(
    id SERIAL PRIMARY KEY,
    user_id int NOT NULL,

    CONSTRAINT user_id_fk FOREIGN KEY (user_id) REFERENCES "user" (id)
);