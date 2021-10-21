SET search_path TO calendar;

ALTER TABLE "user" ADD COLUMN email varchar NOT NULL;
ALTER TABLE "user" ADD COLUMN password varchar NOT NULL;
