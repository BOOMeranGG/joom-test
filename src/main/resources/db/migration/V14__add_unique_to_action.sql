SET search_path TO calendar;

ALTER TABLE action ADD UNIQUE (action_id, calendar_id);