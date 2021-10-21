SET search_path TO calendar;

ALTER TABLE action RENAME TO action_type;
ALTER TABLE action_type RENAME COLUMN type TO name;

CREATE TABLE action(
    id SERIAL PRIMARY KEY,
    is_confirmed boolean NOT NULL DEFAULT false,
    type_id int NOT NULL,
    calendar_id int NOT NULL,
    action_id uuid NOT NULL,

    CONSTRAINT action_type_fk FOREIGN KEY (type_id) REFERENCES action_type (id),
    CONSTRAINT calendar_id_fk FOREIGN KEY (type_id) REFERENCES calendar (id)
);