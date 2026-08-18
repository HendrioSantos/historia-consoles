CREATE TABLE public.geracoes (
    id bigint NOT NULL,
    nome character varying(150) NOT NULL,
    numero_geracao integer NOT NULL,
    geracao_cronologia character varying(50) NOT NULL,
    atual boolean DEFAULT false NOT NULL,
    ativo boolean DEFAULT true NOT NULL,
    slug character varying(150) NOT NULL,
    inicio_periodo date NOT NULL,
    fim_periodo date,
    fato_historico text NOT NULL,
    geracao_empresa_dominante character varying(50) NOT NULL,
    CONSTRAINT geracoes_pkey PRIMARY KEY (id),
    CONSTRAINT geracoes_nome_key UNIQUE (nome),
    CONSTRAINT geracoes_slug_key UNIQUE (slug)
);

CREATE SEQUENCE public.geracoes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.geracoes ALTER COLUMN id SET DEFAULT nextval('public.geracoes_id_seq'::regclass);
