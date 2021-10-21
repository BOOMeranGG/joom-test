SET search_path TO calendar;

ALTER TABLE meeting ADD COLUMN user_creator_id int NOT NULL;
ALTER TABLE meeting ADD FOREIGN KEY (user_creator_id) REFERENCES "user" (id);