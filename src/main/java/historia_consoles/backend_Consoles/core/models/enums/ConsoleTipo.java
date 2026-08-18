package historia_consoles.backend_Consoles.core.models.enums;

import lombok.Getter;

@Getter
public enum ConsoleTipo {
    MESA("Console de Mesa / Doméstico"),
    PORTATIL("Console Portátil"),
    HIBRIDO("Console Híbrido"),
    MICRO_CONSOLE("Micro Console / Plug & Play"),
    MINI_CONSOLE("Mini Console Retrô de Colecionador");

    private final String descricao;

    ConsoleTipo(String descricao) {
        this.descricao = descricao;
    }
}
