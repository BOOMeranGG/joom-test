SET search_path TO calendar;

ALTER TABLE meeting ADD COLUMN is_private bool DEFAULT false;