ALTER TABLE console_aud ADD COLUMN data_criacao TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE console_aud ADD COLUMN data_ultima_modificacao TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE console_aud ADD COLUMN criado_por VARCHAR(255);
ALTER TABLE console_aud ADD COLUMN modificado_por VARCHAR(255);