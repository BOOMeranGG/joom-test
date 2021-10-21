SET search_path TO calendar;

ALTER TABLE action DROP COLUMN date_time;
ALTER TABLE meeting ADD COLUMN date_time timestamptz NOT NULL;