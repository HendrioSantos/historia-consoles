package historia_consoles.backendConsoles.core.repository;

import historia_consoles.backend_Consoles.aplicacao.BackendConsolesApplication;
import historia_consoles.backend_Consoles.core.models.entities.Periodo;
import historia_consoles.backend_Consoles.core.models.entities.Geracao;
import historia_consoles.backend_Consoles.core.models.enums.GeracaoCronologia;
import historia_consoles.backend_Consoles.core.models.enums.GeracaoEmpresaDominante;
import historia_consoles.backend_Consoles.filtradores.FiltradorSlug;
import historia_consoles.backend_Consoles.core.repository.GeracaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

@DataJpaTest
@ContextConfiguration(classes = BackendConsolesApplication.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DisplayName("Teste de geração")
class GeracaoRepositoryTest {

    @Autowired
    private GeracaoRepository repository;

    private Geracao criarGeracao(){
        return Geracao.builder()
                .nome("Primeira")
                .numeroGeracao(1)
                .periodo(Periodo.builder()
                        .inicio(LocalDate.now())
                        .fim(LocalDate.now())
                        .build())
                .ativo(true)
                .atual(false)
                .slug(FiltradorSlug.gerarSlug("Primeira"))
                .fatoHistorico("Fatidico dia")
                .geracaoCronologia(GeracaoCronologia.PIONEIROS_OU_VINTAGE)
                .geracaoEmpresaDominante(GeracaoEmpresaDominante.ATARI_DOMINANTE)
                .build();
    }

    private Geracao criarGeracaoNomeDiferente(){
        return Geracao.builder()
                .nome("Primeiraa")
                .numeroGeracao(1)
                .periodo(Periodo.builder()
                        .inicio(LocalDate.now())
                        .fim(LocalDate.now())
                        .build())
                .ativo(true)
                .atual(false)
                .slug(FiltradorSlug.gerarSlug("Primeiraa"))
                .fatoHistorico("Fatidico dia")
                .geracaoCronologia(GeracaoCronologia.PIONEIROS_OU_VINTAGE)
                .geracaoEmpresaDominante(GeracaoEmpresaDominante.ATARI_DOMINANTE)
                .build();
    }

    private Geracao criarGeracaoInativa() {
        return Geracao.builder()
                .nome("Primeira2")
                .numeroGeracao(1)
                .periodo(Periodo.builder()
                        .inicio(LocalDate.now())
                        .fim(LocalDate.now())
                        .build())
                .ativo(false)
                .atual(false)
                .slug(FiltradorSlug.gerarSlug("Primeira2"))
                .fatoHistorico("Fatidico dia")
                .geracaoCronologia(GeracaoCronologia.PIONEIROS_OU_VINTAGE)
                .geracaoEmpresaDominante(GeracaoEmpresaDominante.ATARI_DOMINANTE)
                .build();
    }

    @Test
    void deveCadastrarUmaGeracaoComId_E_AtivoTrueComSucesso(){
        var geracao = criarGeracao();

        var geracaoSalva = repository.saveAndFlush(geracao);
        var geracaoBd = repository.findByIdAndAtivoTrue(geracaoSalva.getId());

        assertTrue(geracaoBd.isPresent());
        assertEquals(geracaoSalva, geracaoBd.get());
    }

    @Test
    void naoDeveEncontrarGeracaoComAtivoFalse() {
        var geracao = criarGeracaoInativa();

        var geracaoSalva = repository.saveAndFlush(geracao);
        var geracaoBd = repository.findByIdAndAtivoTrue(geracaoSalva.getId());

        assertFalse(geracaoBd.isPresent());
        assertTrue(geracaoBd.isEmpty());
    }

    @Test
    void naoEncontrarSlugIgnoreCaseAndAtivoTrue() {
        var slug = "";
        var geracao = repository.findBySlugIgnoreCaseAndAtivoTrue(slug);

        assertFalse(geracao.isPresent());
        assertTrue(geracao.isEmpty());
    }

    @Test
    void deveEncontrarGeracoesAtivas() {
        var geracao = criarGeracao();
        var geracao2 = criarGeracaoNomeDiferente();

        var geracaoSalva = repository.saveAndFlush(geracao);
        var geracaoSalva2 = repository.saveAndFlush(geracao2);

        Pageable paginacao = PageRequest.of(0, 10);
        Page<Geracao> paginaGeracao = repository.findAllByAtivoTrue(paginacao);

        var listaGeracao = paginaGeracao.getContent();

        var geracaoAtiva = listaGeracao.contains(geracaoSalva);
        var geracaoAtiva2 = listaGeracao.contains(geracaoSalva2);

        assertTrue(geracaoAtiva);
        assertTrue(geracaoAtiva2);
    }

    @Test
    void naoDeveEncontrarGeracoesInativas() {
        var geracao = criarGeracao();
        var geracao2 = criarGeracaoInativa();

        repository.saveAndFlush(geracao);
        repository.saveAndFlush(geracao2);

        Pageable paginacao = PageRequest.of(0, 10);
        Page<Geracao> paginaGeracao = repository.findAllByAtivoTrue(paginacao);

        var listaGeracao = paginaGeracao.getContent();

        var geracaoAtiva = listaGeracao.contains(geracao);
        var geracaoInativa = listaGeracao.contains(geracao2);

        assertTrue(geracaoAtiva);
        assertFalse(geracaoInativa);
    }
}