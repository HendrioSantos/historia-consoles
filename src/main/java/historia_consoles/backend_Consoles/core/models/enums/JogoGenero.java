package historia_consoles.backend_Consoles.core.models.enums;

import lombok.Getter;

@Getter
public enum JogoGenero {
    PONG_OU_TELECORES("Pong / Telecores"),
    ARCADE("Arcade / Fliperama"),
    ACAO("Ação"),
    AVENTURA("Aventura"),
    PLATAFORMA("Plataforma"),
    RPG("RPG / Role-Playing Game"),
    ESTRATEGIA("Estratégia"),
    CORRIDA("Corrida"),
    ESPORTE("Esporte"),
    TIRO_OU_SHOOTER("Tiro / Shooter"),
    LUTA("Luta"),
    SIMULACAO("Simulação"),
    PUZZLE("Quebra-Cabeça");

    private final String descricao;

    JogoGenero(String descricao) {
        this.descricao = descricao;
    }


}
