CREATE TABLE public.consoles (
    id bigint NOT NULL,
    nome character varying(150) NOT NULL,
    fabricante character varying(150) NOT NULL,
    publicadora character varying(150) NOT NULL,
    slug character varying(150) NOT NULL,
    imagem_url character varying(255),
    ativo boolean DEFAULT true NOT NULL,
    descontinuado boolean DEFAULT true NOT NULL,
    geracao_id bigint,
    cpu character varying(150),
    gpu character varying(150),
    ram character varying(150),
    armazenamento character varying(150),
    midia character varying(150),
    resolucao character varying(150),
    inicio_periodo date NOT NULL,
    fim_periodo date,
    unidades_vendidas character varying(50) NOT NULL,
    preco_lancamento character varying(50),
    console_tipo character varying(50),
    retrocompatibilidade boolean DEFAULT false NOT NULL,
    CONSTRAINT consoles_pkey PRIMARY KEY (id),
    CONSTRAINT consoles_nome_key UNIQUE (nome),
    CONSTRAINT consoles_slug_key UNIQUE (slug),
    CONSTRAINT fk_consoles_geracao FOREIGN KEY (geracao_id) REFERENCES public.geracoes(id) ON DELETE SET NULL
);

CREATE SEQUENCE public.consoles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.consoles ALTER COLUMN id SET DEFAULT nextval('public.consoles_id_seq'::regclass);
