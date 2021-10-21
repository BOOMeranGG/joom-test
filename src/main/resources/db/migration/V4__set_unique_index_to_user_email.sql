SET search_path TO calendar;

CREATE UNIQUE INDEX user_email_idx
    on "user" (email);