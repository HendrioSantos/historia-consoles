package historia_consoles.backend_Consoles.core.models.enums;

import lombok.Getter;

@Getter
public enum JogoMidiaOriginal {
    EMBUTIDO_MEMORIA("Embutido na Memória"),
    CARTUCHO("Cartucho / ROM"),
    CARTUCHO_PROPRIETARIO("Cartucho Proprietário"),
    CD_ROM("CD-ROM"),
    GD_ROM("GD-ROM"),
    DVD_ROM("DVD-ROM"),
    BLU_RAY("Blu-ray Disc"),
    DIGITAL("Distribuição Digital / Download");

    private final String descricao;

    JogoMidiaOriginal(String descricao) {
        this.descricao = descricao;
    }
}
