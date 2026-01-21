-- public."role" definition

-- Drop table

-- DROP TABLE public."role";

CREATE TABLE public."role" (
	id int8 NOT NULL,
	"name" varchar(255) NULL,
	CONSTRAINT role_pkey PRIMARY KEY (id)
);


-- public.users definition

-- Drop table

-- DROP TABLE public.users;

CREATE TABLE public.users (
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


-- public."event" definition

-- Drop table

-- DROP TABLE public."event";

CREATE TABLE public."event" (
	creation_date timestamp(6) NULL,
	"date" timestamp(6) NOT NULL,
	modification_date timestamp(6) NULL,
	creator_id uuid NULL,
	id uuid NOT NULL,
	pictureuri varchar(4096) NULL,
	description varchar(255) NOT NULL,
	"location" varchar(255) NOT NULL,
	title varchar(255) NOT NULL,
	is_private bool DEFAULT true NULL,
	CONSTRAINT event_pkey PRIMARY KEY (id),
	CONSTRAINT fk1h6eb0wh6dq1j6h52570b4keg FOREIGN KEY (creator_id) REFERENCES public.users(id)
);


-- public.event_event_admins definition

-- Drop table

-- DROP TABLE public.event_event_admins;

CREATE TABLE public.event_event_admins (
	administered_events_id uuid NOT NULL,
	event_admins_id uuid NOT NULL,
	CONSTRAINT fk536isow46vbmofdyrws7rqiiy FOREIGN KEY (event_admins_id) REFERENCES public.users(id),
	CONSTRAINT fkku49a4jkyexfeh22eelp24ll7 FOREIGN KEY (administered_events_id) REFERENCES public."event"(id)
);


-- public.event_participants definition

-- Drop table

-- DROP TABLE public.event_participants;

CREATE TABLE public.event_participants (
	attended_events_id uuid NOT NULL,
	participants_id uuid NOT NULL,
	CONSTRAINT fk8gnffqcvvxcfijv2ycdpnsarq FOREIGN KEY (participants_id) REFERENCES public.users(id),
	CONSTRAINT fkoxwssrwow02cmts1jd3nauvxa FOREIGN KEY (attended_events_id) REFERENCES public."event"(id)
);


-- public.medium definition

-- Drop table

-- DROP TABLE public.medium;

CREATE TABLE public.medium (
	event_id uuid NOT NULL,
	id uuid NOT NULL,
	mediumuri varchar(4096) NOT NULL,
	creator_id uuid NOT NULL,
	CONSTRAINT medium_pkey PRIMARY KEY (id),
	CONSTRAINT fk3w1fuk3erkwy3d8nyiwenjx0t FOREIGN KEY (event_id) REFERENCES public."event"(id),
	CONSTRAINT fk_medium_creator FOREIGN KEY (creator_id) REFERENCES public.users(id)
);


-- public.user_comment definition

-- Drop table

-- DROP TABLE public.user_comment;

CREATE TABLE public.user_comment (
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


-- public.user_roles definition

-- Drop table

-- DROP TABLE public.user_roles;

CREATE TABLE public.user_roles (
	user_id uuid NOT NULL,
	role_id int8 NOT NULL,
	CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role_id),
	CONSTRAINT fkhfh9dx7w3ubf1co1vdev94g3f FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT fkrhfovtciq1l558cw6udg0h0d3 FOREIGN KEY (role_id) REFERENCES public."role"(id)
);