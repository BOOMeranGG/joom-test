SET search_path TO calendar;

ALTER TABLE action ADD COLUMN date_time timestamptz NOT NULL;