package historia_consoles.backend_Consoles.core.models.enums;

import lombok.Getter;

@Getter
public enum GeracaoEmpresaDominante {
    MAGNAVOX_ATARI("Magnavox e Atari"),
    ATARI_DOMINANTE("Atari"),
    NINTENDO_DOMINANTE("Nintendo (Era 8-bits)"),
    NINTENDO_SEGA("Nintendo e Sega (Era 16-bits)"),
    SONY_DOMINANTE("Sony PlayStation (Era 32/64 bits)"),
    SONY_NINTENDO_MICROSOFT("Sony, Nintendo e Microsoft"),
    NINTENDO_WII_LIDER("Nintendo (Era Wii)"),
    SONY_PLAYSTATION_4_LIDER("Sony (Era PS4)"),
    ATUAL_MERCADO("Disputa Aberta (Geração Atual)");

    private final String descricao;

    GeracaoEmpresaDominante(String descricao) {
        this.descricao = descricao;
    }
}
