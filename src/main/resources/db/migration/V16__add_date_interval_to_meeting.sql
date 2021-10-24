SET search_path TO calendar;

ALTER TABLE meeting RENAME COLUMN date_time TO date_time_from;
ALTER TABLE meeting ADD COLUMN date_time timestamptz;