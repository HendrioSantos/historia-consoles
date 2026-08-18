CREATE TABLE public.geracao_aud (
    id bigint NOT NULL,
    rev bigint NOT NULL,
    revtype smallint,
    nome character varying(255),
    numero_geracao integer,
    geracao_cronologia character varying(255),
    atual boolean,
    ativo boolean,
    slug character varying(255),
    inicio_periodo date,
    fim_periodo date,
    fato_historico text,
    geracao_empresa_dominante character varying(255),
    CONSTRAINT geracao_aud_pkey PRIMARY KEY (id, rev),
    CONSTRAINT fk_geracao_aud_revinfo FOREIGN KEY (rev) REFERENCES public.revinfo(rev)
);
