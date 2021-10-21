SET search_path TO calendar;

ALTER TABLE meeting ADD COLUMN description varchar;
ALTER TABLE meeting ADD COLUMN video_conference_link varchar;