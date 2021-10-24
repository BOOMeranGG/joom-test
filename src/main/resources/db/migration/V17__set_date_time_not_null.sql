SET search_path TO calendar;

ALTER TABLE meeting RENAME COLUMN date_time TO date_time_to;
ALTER TABLE meeting ALTER COLUMN date_time_to SET NOT NULL;