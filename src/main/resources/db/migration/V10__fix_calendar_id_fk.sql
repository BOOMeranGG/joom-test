SET search_path TO calendar;

ALTER TABLE action DROP COLUMN type_id;
ALTER TABLE action DROP COLUMN calendar_id;

ALTER TABLE action ADD COLUMN type_id int NOT NULL REFERENCES action_type (id);
ALTER TABLE action ADD COLUMN calendar_id int NOT NULL REFERENCES calendar (id);