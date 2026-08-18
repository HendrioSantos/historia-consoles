CREATE TABLE public.usuarios (
    id bigint NOT NULL,
    login character varying(150) NOT NULL,
    senha character varying(255) NOT NULL,
    role character varying(50) NOT NULL,
    ativo boolean DEFAULT true NOT NULL,
    CONSTRAINT usuarios_pkey PRIMARY KEY (id),
    CONSTRAINT usuarios_login_key UNIQUE (login)
);

CREATE SEQUENCE public.usuarios_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.usuarios ALTER COLUMN id SET DEFAULT nextval('public.usuarios_id_seq'::regclass);
