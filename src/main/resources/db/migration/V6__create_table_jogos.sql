CREATE TABLE public.jogos (
    id bigint NOT NULL,
    nome character varying(150) NOT NULL,
    desenvolvedora character varying(150) NOT NULL,
    publicadora character varying(150) NOT NULL,
    console_id bigint,
    ativo boolean DEFAULT true NOT NULL,
    slug character varying(150) NOT NULL,
    jogo_status character varying(50) DEFAULT 'ENTRADA_SISTEMA'::character varying NOT NULL,
    imagem_url text,
    url_video text,
    jogogenero character varying(50) NOT NULL,
    nota_critica integer NOT NULL,
    modo_jogo text NOT NULL,
    diretor_criador character varying(150) NOT NULL,
    jogo_midia_original character varying(50) NOT NULL,
    tamanho_arquivo character varying(30) NOT NULL,
    retrocompatibilidade boolean DEFAULT false NOT NULL,
    CONSTRAINT jogos_pkey PRIMARY KEY (id),
    CONSTRAINT jogos_slug_key UNIQUE (slug),
    CONSTRAINT fk_jogo_console FOREIGN KEY (console_id) REFERENCES public.consoles(id) ON DELETE CASCADE
);

CREATE SEQUENCE public.jogos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.jogos ALTER COLUMN id SET DEFAULT nextval('public.jogos_id_seq'::regclass);
