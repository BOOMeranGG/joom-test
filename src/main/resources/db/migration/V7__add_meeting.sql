SET search_path TO calendar;

CREATE TABLE meeting(
    guid uuid PRIMARY KEY DEFAULT gen_random_uuid()
);