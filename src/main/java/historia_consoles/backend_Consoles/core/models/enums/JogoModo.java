package historia_consoles.backend_Consoles.core.models.enums;

import lombok.Getter;

@Getter
public enum JogoModo {
    MULTIPLAYER("Varios jogadores simultanêos"),
    SOLO("Somente um único jogador"),
    SQUAD("Número limitado de jogadores, normalmente até 3 ou 4");

    private final String descricao;

    JogoModo(String descricao) {
        this.descricao = descricao;
    }
}
