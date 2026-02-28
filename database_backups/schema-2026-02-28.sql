--
-- PostgreSQL database dump
--

-- Dumped from database version 17.8 (6108b59)
-- Dumped by pg_dump version 17.5 (Debian 17.5-1.pgdg120+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: event; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.event (
    creation_date timestamp(6) without time zone,
    date timestamp(6) without time zone NOT NULL,
    modification_date timestamp(6) without time zone,
    creator_id uuid,
    id uuid NOT NULL,
    pictureuri character varying(4096),
    description character varying(2000) NOT NULL,
    location character varying(255) NOT NULL,
    title character varying(255) NOT NULL,
    is_private boolean DEFAULT true
);


ALTER TABLE public.event OWNER TO neondb_owner;

--
-- Name: event_event_admins; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.event_event_admins (
    administered_events_id uuid NOT NULL,
    event_admins_id uuid NOT NULL
);


ALTER TABLE public.event_event_admins OWNER TO neondb_owner;

--
-- Name: event_participants; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.event_participants (
    attended_events_id uuid NOT NULL,
    participants_id uuid NOT NULL
);


ALTER TABLE public.event_participants OWNER TO neondb_owner;

--
-- Name: medium; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.medium (
    event_id uuid NOT NULL,
    id uuid NOT NULL,
    mediumuri character varying(4096) NOT NULL,
    creator_id uuid NOT NULL,
    creation_date timestamp(6) with time zone,
    modification_date timestamp(6) with time zone
);


ALTER TABLE public.medium OWNER TO neondb_owner;

--
-- Name: role; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.role (
    id bigint NOT NULL,
    name character varying(255),
    creation_date timestamp(6) with time zone,
    modification_date timestamp(6) with time zone
);


ALTER TABLE public.role OWNER TO neondb_owner;

--
-- Name: role_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.role_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.role_seq OWNER TO neondb_owner;

--
-- Name: user_comment; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.user_comment (
    creation_date timestamp(6) without time zone,
    modification_date timestamp(6) without time zone,
    author_id uuid NOT NULL,
    event_id uuid,
    id uuid NOT NULL,
    text character varying(255) NOT NULL
);


ALTER TABLE public.user_comment OWNER TO neondb_owner;

--
-- Name: user_roles; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.user_roles (
    user_id uuid NOT NULL,
    role_id bigint NOT NULL
);


ALTER TABLE public.user_roles OWNER TO neondb_owner;

--
-- Name: users; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.users (
    is_active boolean NOT NULL,
    register_date timestamp(6) without time zone,
    id uuid NOT NULL,
    pictureuri character varying(4096),
    email character varying(255) NOT NULL,
    first_name character varying(255) NOT NULL,
    location character varying(255),
    old_password character varying(255),
    password character varying(255) NOT NULL,
    salutation character varying(255) NOT NULL,
    surname character varying(255) NOT NULL,
    username character varying(255) NOT NULL,
    about_me character varying(255),
    modification_date timestamp(6) with time zone,
    country character varying(255),
    deletion_date timestamp(6) with time zone,
    is_deleted boolean DEFAULT false
);


ALTER TABLE public.users OWNER TO neondb_owner;

--
-- Name: event event_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.event
    ADD CONSTRAINT event_pkey PRIMARY KEY (id);


--
-- Name: medium medium_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.medium
    ADD CONSTRAINT medium_pkey PRIMARY KEY (id);


--
-- Name: role role_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.role
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);


--
-- Name: user_comment user_comment_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.user_comment
    ADD CONSTRAINT user_comment_pkey PRIMARY KEY (id);


--
-- Name: user_roles user_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- Name: idx_event_date; Type: INDEX; Schema: public; Owner: neondb_owner
--

CREATE INDEX idx_event_date ON public.event USING btree (date);


--
-- Name: idx_event_location; Type: INDEX; Schema: public; Owner: neondb_owner
--

CREATE INDEX idx_event_location ON public.event USING btree (location);


--
-- Name: idx_event_private; Type: INDEX; Schema: public; Owner: neondb_owner
--

CREATE INDEX idx_event_private ON public.event USING btree (is_private);


--
-- Name: idx_event_title; Type: INDEX; Schema: public; Owner: neondb_owner
--

CREATE INDEX idx_event_title ON public.event USING btree (title);


--
-- Name: idx_user_active; Type: INDEX; Schema: public; Owner: neondb_owner
--

CREATE INDEX idx_user_active ON public.users USING btree (is_active);


--
-- Name: idx_user_email; Type: INDEX; Schema: public; Owner: neondb_owner
--

CREATE INDEX idx_user_email ON public.users USING btree (email);


--
-- Name: idx_user_first_name; Type: INDEX; Schema: public; Owner: neondb_owner
--

CREATE INDEX idx_user_first_name ON public.users USING btree (first_name);


--
-- Name: idx_user_surname; Type: INDEX; Schema: public; Owner: neondb_owner
--

CREATE INDEX idx_user_surname ON public.users USING btree (surname);


--
-- Name: idx_user_username; Type: INDEX; Schema: public; Owner: neondb_owner
--

CREATE INDEX idx_user_username ON public.users USING btree (username);


--
-- Name: event fk1h6eb0wh6dq1j6h52570b4keg; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.event
    ADD CONSTRAINT fk1h6eb0wh6dq1j6h52570b4keg FOREIGN KEY (creator_id) REFERENCES public.users(id);


--
-- Name: medium fk3w1fuk3erkwy3d8nyiwenjx0t; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.medium
    ADD CONSTRAINT fk3w1fuk3erkwy3d8nyiwenjx0t FOREIGN KEY (event_id) REFERENCES public.event(id);


--
-- Name: event_event_admins fk536isow46vbmofdyrws7rqiiy; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.event_event_admins
    ADD CONSTRAINT fk536isow46vbmofdyrws7rqiiy FOREIGN KEY (event_admins_id) REFERENCES public.users(id);


--
-- Name: user_comment fk6sgvqspyk2p1ktxdtxktpjers; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.user_comment
    ADD CONSTRAINT fk6sgvqspyk2p1ktxdtxktpjers FOREIGN KEY (author_id) REFERENCES public.users(id);


--
-- Name: event_participants fk8gnffqcvvxcfijv2ycdpnsarq; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.event_participants
    ADD CONSTRAINT fk8gnffqcvvxcfijv2ycdpnsarq FOREIGN KEY (participants_id) REFERENCES public.users(id);


--
-- Name: user_comment fk9u2m2s1qjbpckok3lbxktg75b; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.user_comment
    ADD CONSTRAINT fk9u2m2s1qjbpckok3lbxktg75b FOREIGN KEY (event_id) REFERENCES public.event(id);


--
-- Name: medium fk_medium_creator; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.medium
    ADD CONSTRAINT fk_medium_creator FOREIGN KEY (creator_id) REFERENCES public.users(id);


--
-- Name: user_roles fkhfh9dx7w3ubf1co1vdev94g3f; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT fkhfh9dx7w3ubf1co1vdev94g3f FOREIGN KEY (user_id) REFERENCES public.users(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: event_event_admins fkku49a4jkyexfeh22eelp24ll7; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.event_event_admins
    ADD CONSTRAINT fkku49a4jkyexfeh22eelp24ll7 FOREIGN KEY (administered_events_id) REFERENCES public.event(id);


--
-- Name: event_participants fkoxwssrwow02cmts1jd3nauvxa; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.event_participants
    ADD CONSTRAINT fkoxwssrwow02cmts1jd3nauvxa FOREIGN KEY (attended_events_id) REFERENCES public.event(id);


--
-- Name: user_roles fkrhfovtciq1l558cw6udg0h0d3; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT fkrhfovtciq1l558cw6udg0h0d3 FOREIGN KEY (role_id) REFERENCES public.role(id);


--
-- Name: DEFAULT PRIVILEGES FOR SEQUENCES; Type: DEFAULT ACL; Schema: public; Owner: cloud_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE cloud_admin IN SCHEMA public GRANT ALL ON SEQUENCES TO neon_superuser WITH GRANT OPTION;


--
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: public; Owner: cloud_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE cloud_admin IN SCHEMA public GRANT ALL ON TABLES TO neon_superuser WITH GRANT OPTION;


--
-- PostgreSQL database dump complete
--

