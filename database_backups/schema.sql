-- "role" definition

-- Drop table

-- DROP TABLE "role";

CREATE TABLE IF NOT EXISTS "role" (
	id int8 NOT NULL,
	"name" varchar(255) NULL,
	CONSTRAINT role_pkey PRIMARY KEY (id)
);


-- users definition

-- Drop table

-- DROP TABLE users;

CREATE TABLE IF NOT EXISTS users (
	is_active bool NOT NULL,
	register_date timestamp(6) NULL,
	id uuid NOT NULL,
	pictureuri varchar(4096) NULL,
	email varchar(255) NOT NULL,
	first_name varchar(255) NOT NULL,
	"location" varchar(255) NULL,
	old_password varchar(255) NULL,
	"password" varchar(255) NOT NULL,
	salutation varchar(255) NOT NULL,
	surname varchar(255) NOT NULL,
	username varchar(255) NOT NULL,
	about_me varchar(255) NULL,
	CONSTRAINT users_pkey PRIMARY KEY (id),
	CONSTRAINT users_username_key UNIQUE (username)
);


-- "event" definition

-- Drop table

-- DROP TABLE "event";

CREATE TABLE IF NOT EXISTS "event" (
	creation_date timestamp(6) NULL,
	"date" timestamp(6) NOT NULL,
	modification_date timestamp(6) NULL,
	creator_id uuid NULL,
	id uuid NOT NULL,
	pictureuri varchar(4096) NULL,
	description varchar(500) NOT NULL,
	"location" varchar(255) NOT NULL,
	title varchar(255) NOT NULL,
	is_private bool DEFAULT true NULL,
	CONSTRAINT event_pkey PRIMARY KEY (id),
	CONSTRAINT fk1h6eb0wh6dq1j6h52570b4keg FOREIGN KEY (creator_id) REFERENCES public.users(id)
);


-- event_event_admins definition

-- Drop table

-- DROP TABLE event_event_admins;

CREATE TABLE IF NOT EXISTS event_event_admins (
	administered_events_id uuid NOT NULL,
	event_admins_id uuid NOT NULL,
	CONSTRAINT fk536isow46vbmofdyrws7rqiiy FOREIGN KEY (event_admins_id) REFERENCES public.users(id),
	CONSTRAINT fkku49a4jkyexfeh22eelp24ll7 FOREIGN KEY (administered_events_id) REFERENCES public."event"(id) ON DELETE CASCADE
);


-- event_participants definition

-- Drop table

-- DROP TABLE event_participants;

CREATE TABLE IF NOT EXISTS event_participants (
	attended_events_id uuid NOT NULL,
	participants_id uuid NOT NULL,
	CONSTRAINT fk8gnffqcvvxcfijv2ycdpnsarq FOREIGN KEY (participants_id) REFERENCES public.users(id),
	CONSTRAINT fkoxwssrwow02cmts1jd3nauvxa FOREIGN KEY (attended_events_id) REFERENCES public."event"(id)
);


-- medium definition

-- Drop table

-- DROP TABLE medium;

CREATE TABLE IF NOT EXISTS medium (
	event_id uuid NOT NULL,
	id uuid NOT NULL,
	mediumuri varchar(4096) NOT NULL,
	creator_id uuid NOT NULL,
	CONSTRAINT medium_pkey PRIMARY KEY (id),
	CONSTRAINT fk3w1fuk3erkwy3d8nyiwenjx0t FOREIGN KEY (event_id) REFERENCES public."event"(id),
	CONSTRAINT fk_medium_creator FOREIGN KEY (creator_id) REFERENCES public.users(id)
);


-- user_comment definition

-- Drop table

-- DROP TABLE user_comment;

CREATE TABLE IF NOT EXISTS user_comment (
	creation_date timestamp(6) NULL,
	modification_date timestamp(6) NULL,
	author_id uuid NOT NULL,
	event_id uuid NULL,
	id uuid NOT NULL,
	"text" varchar(255) NOT NULL,
	pictureuri varchar(4096) NULL,
	CONSTRAINT user_comment_pkey PRIMARY KEY (id),
	CONSTRAINT fk6sgvqspyk2p1ktxdtxktpjers FOREIGN KEY (author_id) REFERENCES public.users(id),
	CONSTRAINT fk9u2m2s1qjbpckok3lbxktg75b FOREIGN KEY (event_id) REFERENCES public."event"(id)
);


-- user_roles definition

-- Drop table

-- DROP TABLE user_roles;

CREATE TABLE IF NOT EXISTS user_roles (
	user_id uuid NOT NULL,
	role_id int8 NOT NULL,
	CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role_id),
	CONSTRAINT fkhfh9dx7w3ubf1co1vdev94g3f FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT fkrhfovtciq1l558cw6udg0h0d3 FOREIGN KEY (role_id) REFERENCES public."role"(id)
);