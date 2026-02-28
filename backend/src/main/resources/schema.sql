CREATE TABLE IF NOT EXISTS "event" (
    id uuid NOT NULL PRIMARY KEY,
    creation_date timestamp(6) without time zone,
    date timestamp(6) without time zone NOT NULL,
    modification_date timestamp(6) without time zone,
    creator_id uuid,
    pictureuri character varying(4096),
    description character varying(2000) NOT NULL,
    location character varying(255) NOT NULL,
    title character varying(255) NOT NULL,
    is_private boolean DEFAULT true
);

CREATE TABLE IF NOT EXISTS event_event_admins (
    administered_events_id uuid NOT NULL,
    event_admins_id uuid NOT NULL,
    PRIMARY KEY (administered_events_id, event_admins_id)
);

CREATE TABLE IF NOT EXISTS event_participants (
    attended_events_id uuid NOT NULL,
    participants_id uuid NOT NULL,
    PRIMARY KEY (attended_events_id, participants_id)
);

CREATE TABLE IF NOT EXISTS medium (
    id uuid NOT NULL PRIMARY KEY,
    event_id uuid NOT NULL,
    mediumuri character varying(4096) NOT NULL,
    creator_id uuid NOT NULL,
    creation_date timestamp(6) with time zone,
    modification_date timestamp(6) with time zone
);

CREATE TABLE IF NOT EXISTS role (
    id bigint NOT NULL PRIMARY KEY,
    name character varying(255),
    creation_date timestamp(6) with time zone,
    modification_date timestamp(6) with time zone
);

CREATE TABLE IF NOT EXISTS user_comment (
    id uuid NOT NULL PRIMARY KEY,
    creation_date timestamp(6) without time zone,
    modification_date timestamp(6) without time zone,
    author_id uuid NOT NULL,
    event_id uuid,
    text character varying(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id uuid NOT NULL,
    role_id bigint NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS users (
    id uuid NOT NULL PRIMARY KEY,
    is_active boolean NOT NULL,
    register_date timestamp(6) without time zone,
    pictureuri character varying(4096),
    email character varying(255) NOT NULL,
    first_name character varying(255) NOT NULL,
    location character varying(255),
    old_password character varying(255),
    password character varying(255) NOT NULL,
    salutation character varying(255) NOT NULL,
    surname character varying(255) NOT NULL,
    username character varying(255) NOT NULL UNIQUE,
    about_me character varying(255),
    modification_date timestamp(6) with time zone,
    country character varying(255),
    deletion_date timestamp(6) with time zone,
    is_deleted boolean DEFAULT false
);

-- Foreign Keys
ALTER TABLE "event" ADD CONSTRAINT fk_event_creator
    FOREIGN KEY (creator_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE SET NULL
    NOT VALID;

ALTER TABLE medium ADD CONSTRAINT fk_medium_event
    FOREIGN KEY (event_id) REFERENCES "event"(id)
    NOT VALID;

ALTER TABLE medium ADD CONSTRAINT fk_medium_creator
    FOREIGN KEY (creator_id) REFERENCES users(id)
    NOT VALID;

ALTER TABLE event_event_admins ADD CONSTRAINT fk_event_admins_event
    FOREIGN KEY (administered_events_id) REFERENCES "event"(id)
    NOT VALID;

ALTER TABLE event_event_admins ADD CONSTRAINT fk_event_admins_user
    FOREIGN KEY (event_admins_id) REFERENCES users(id)
    NOT VALID;

ALTER TABLE event_participants ADD CONSTRAINT fk_event_participants_event
    FOREIGN KEY (attended_events_id) REFERENCES "event"(id)
    NOT VALID;

ALTER TABLE event_participants ADD CONSTRAINT fk_event_participants_user
    FOREIGN KEY (participants_id) REFERENCES users(id)
    NOT VALID;

ALTER TABLE user_comment ADD CONSTRAINT fk_comment_author
    FOREIGN KEY (author_id) REFERENCES users(id)
    NOT VALID;

ALTER TABLE user_comment ADD CONSTRAINT fk_comment_event
    FOREIGN KEY (event_id) REFERENCES "event"(id)
    NOT VALID;

ALTER TABLE user_roles ADD CONSTRAINT fk_user_roles_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE
    NOT VALID;

ALTER TABLE user_roles ADD CONSTRAINT fk_user_roles_role
    FOREIGN KEY (role_id) REFERENCES role(id)
    NOT VALID;

-- Indexes
CREATE INDEX IF NOT EXISTS idx_event_date ON "event" USING btree (date);
CREATE INDEX IF NOT EXISTS idx_event_location ON "event" USING btree (location);
CREATE INDEX IF NOT EXISTS idx_event_private ON "event" USING btree (is_private);
CREATE INDEX IF NOT EXISTS idx_event_title ON "event" USING btree (title);
CREATE INDEX IF NOT EXISTS idx_user_active ON users USING btree (is_active);
CREATE INDEX IF NOT EXISTS idx_user_email ON users USING btree (email);
CREATE INDEX IF NOT EXISTS idx_user_first_name ON users USING btree (first_name);
CREATE INDEX IF NOT EXISTS idx_user_surname ON users USING btree (surname);
CREATE INDEX IF NOT EXISTS idx_user_username ON users USING btree (username);