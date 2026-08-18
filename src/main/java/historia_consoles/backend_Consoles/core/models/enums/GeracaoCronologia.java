package historia_consoles.backend_Consoles.core.models.enums;

import lombok.Getter;

@Getter
public enum GeracaoCronologia {

    PIONEIROS_OU_VINTAGE(
            "Pioneiros / Vintage",
            "0 Bits",
            "1ª Geração",
            "Circuitos analógicos integrados; fios, transistores e diodos combinados na placa."
    ),

    ANTIGOS_OU_OLD_SCHOOL(
            "Old School (8-Bits)",
            "8 Bits",
            "2ª e 3ª Geração",
            "Nascimento das CPUs comerciais dedicadas e gráficos pixelados clássicos."
    ),

    RETRO(
            "Retrô (16-Bits)",
            "16 Bits",
            "4ª Geração",
            "A era de ouro do design 2D, rolagem parallax e paletas de cores expandidas."
    ),

    RETRO_MODERNO_OU_LEGACY(
            "Legacy (32/64-Bits)",
            "32 / 64 Bits",
            "5ª e 6ª Geração",
            "A grande revolução dos polígonos, gráficos 3D reais e mídias em CD/DVD."
    ),

    CONSOLES_ATUAIS(
            "Consoles Atuais",
            "64 Bits (x86)",
            "7ª à 9ª Geração",
            "Fim da guerra dos bits. Arquitetura multinúcleo de alta performance e SSDs."
    ),

    PROXIMA_GERACAO(
            "Próxima Geração",
            "A definir",
            "10ª Geração+",
            "O futuro dos sistemas de entretenimento interativo."
    ),

    CRONOLOGIA_NAO_EXISTENTE(
            "Não Existente",
            "N/A",
            "N/A",
            "Registro inválido."
    ),

    CRONOLOGIA_GENERICA(
            "Cronologia Genérica",
            "Variável",
            "Geral",
            "Classificação genérica de plataformas."
    );

    private final String tituloExibicao;
    private final String capacidadeBits;
    private final String erasCorrespondentes;
    private final String descricaoTecnica;

    GeracaoCronologia(String tituloExibicao, String capacidadeBits, String erasCorrespondentes, String descricaoTecnica) {
        this.tituloExibicao = tituloExibicao;
        this.capacidadeBits = capacidadeBits;
        this.erasCorrespondentes = erasCorrespondentes;
        this.descricaoTecnica = descricaoTecnica;
    }

    public static GeracaoCronologia verificarCronologiaGeracao(int valor) {

        if (valor < 1 || valor > 10) return CRONOLOGIA_NAO_EXISTENTE;

        return switch (valor){case 1, 2 -> PIONEIROS_OU_VINTAGE;
            case 3, 4 -> ANTIGOS_OU_OLD_SCHOOL;
            case 5, 6 -> RETRO;
            case 7, 8 -> RETRO_MODERNO_OU_LEGACY;
            case 9 -> CONSOLES_ATUAIS;
            case 10 -> PROXIMA_GERACAO;
            default -> CRONOLOGIA_GENERICA;
        };
    }

}
