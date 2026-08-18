package historia_consoles.backendConsoles.core.service;

import historia_consoles.backend_Consoles.common.InvalidoException;
import historia_consoles.backend_Consoles.core.dto.geracao.GeracaoAtualizar;
import historia_consoles.backend_Consoles.core.dto.geracao.GeracaoCriar;
import historia_consoles.backend_Consoles.core.exclusao.geracao.GeracaoExclusao;
import historia_consoles.backend_Consoles.core.models.entities.Periodo;
import historia_consoles.backend_Consoles.core.models.entities.Geracao;
import historia_consoles.backend_Consoles.core.models.enums.GeracaoCronologia;
import historia_consoles.backend_Consoles.core.models.enums.GeracaoEmpresaDominante;
import historia_consoles.backend_Consoles.core.repository.GeracaoRepository;
import historia_consoles.backend_Consoles.core.service.GeracaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testando Service de Gerações")
class GeracaoServiceTest {

    @InjectMocks
    private GeracaoService service;

    @Mock
    private GeracaoRepository repository;

    @Mock
    private List<GeracaoExclusao> excluir;

    @Test
    @DisplayName("Deve encontrar as gerações pelo id e estar ativo")
    void deveEncontrarGeracoesPeloId_E_EstarAtivo(){
        var geracao = Geracao.builder().id(1L).nome("Primeira").ativo(true).build();

        when(repository.findByIdAndAtivoTrue(geracao.getId())).thenReturn(Optional.of(geracao));

        var resultado = service.encontrarGeracao(1L);
        assertNotNull(resultado);
        assertEquals("Primeira", resultado.getNome());
    }

    @Test
    @DisplayName("Deve criar geração com sucesso")
    void deveCriarGeracaoComSucesso(){
        var dto = new GeracaoCriar("1ª Geração", "Fato histórico marcante sobre os consoles pioneiros.", 1,
                GeracaoCronologia.PIONEIROS_OU_VINTAGE, GeracaoEmpresaDominante.ATARI_DOMINANTE, new Periodo(LocalDate.now(), LocalDate.now()));
        var geracaoSalva = Geracao.builder()
                .id(1L)
                .nome("nome")
                .numeroGeracao(1)
                .geracaoCronologia(GeracaoCronologia.PIONEIROS_OU_VINTAGE)
                .atual(false)
                .slug("nome")
                .build();

        when(repository.save(any(Geracao.class))).thenReturn(geracaoSalva);

        var resultado = service.criarGeracao(dto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("nome", resultado.getNome());

        verify(repository, never()).findByAtualTrue();
    }

    @Test
    @DisplayName("Deve encontrar a geração pelo slug")
    void deveEncontrarPeloSlug(){
        var geracaoSalva = Geracao.builder().id(1L).nome("nome").numeroGeracao(1)
                .geracaoCronologia(GeracaoCronologia.PIONEIROS_OU_VINTAGE).atual(false).slug("nome").build();

        when(repository.findBySlugIgnoreCaseAndAtivoTrue("nome")).thenReturn(Optional.of(geracaoSalva));

        var resultado = service.encontrarGeracaoPorSlug("nome");

        assertNotNull(resultado);
        assertEquals("nome", resultado.getSlug());
    }

    @Test
    @DisplayName("Deve paginar as gerações")
    void devePaginarAsGeracoes(){
        Pageable pagina = PageRequest.of(0, 10);
        var geracaoSalva = Geracao.builder().id(1L).nome("nome").numeroGeracao(1)
                .geracaoCronologia(GeracaoCronologia.PIONEIROS_OU_VINTAGE).atual(false).slug("nome").build();

        var listaGeracao = List.of(geracaoSalva);
        Page<Geracao> paginaGeracao = new PageImpl<>(listaGeracao, pagina, listaGeracao.size());

        when(repository.findAllByAtivoTrue(pagina)).thenReturn(paginaGeracao);

        var resultado = service.listarPaginas(pagina);

        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertEquals("nome", resultado.getContent().get(0).nome());
    }

    @Test
    @DisplayName("Deve desativar geração atual antiga quando cadastrar uma nova geração atual")
    void deveDesativarGeracaoAntigaQuandoNovaForAtual(){
        var dto = new GeracaoCriar("1ª Geração", "Fato histórico marcante sobre os consoles pioneiros.", 1,
                GeracaoCronologia.PIONEIROS_OU_VINTAGE, GeracaoEmpresaDominante.ATARI_DOMINANTE, new Periodo(LocalDate.now(), LocalDate.now()));

        var geracaoAntiga = Geracao.builder().id(1L).nome("nome").numeroGeracao(1)
                .geracaoCronologia(GeracaoCronologia.PIONEIROS_OU_VINTAGE).atual(false).slug("nome").build();

        var geracaoAtual = Geracao.builder().id(1L).nome("nome").numeroGeracao(1)
                .geracaoCronologia(GeracaoCronologia.PIONEIROS_OU_VINTAGE).atual(true).slug("nome").build();

        when(repository.findByAtualTrue()).thenReturn(Optional.of(geracaoAntiga));
        when(repository.save(any(Geracao.class))).thenReturn(geracaoAtual);

        var resultado = service.criarGeracao(dto);

        assertNotNull(resultado);
        assertTrue(resultado.isAtual());
        assertFalse(geracaoAntiga.isAtual());

        verify(repository).save(geracaoAntiga);
    }

    @Test
    @DisplayName("Deve atualizar as informações da geração com sucesso")
    void deveAtualizarGeracaoComSucesso() {
        var idExistente = 1L;
        var dto = new GeracaoAtualizar("nomeAtualizado", 1, "Fato histórico atualizado",
                GeracaoCronologia.PIONEIROS_OU_VINTAGE, GeracaoEmpresaDominante.ATARI_DOMINANTE,
                new Periodo(LocalDate.now(), LocalDate.now()), false, true);

        var geracaoAntiga = Geracao.builder().id(idExistente).nome("Nome Antigo").ativo(true).build();
        var geracaoAtualizada = Geracao.builder().id(idExistente).nome("nomeAtualizado").ativo(true).build();

        when(repository.findByIdAndAtivoTrue(idExistente)).thenReturn(Optional.of(geracaoAntiga));
        when(repository.save(any(Geracao.class))).thenReturn(geracaoAtualizada);

        var resultado = service.atualizarGeracao(idExistente, dto);

        assertNotNull(resultado);
        assertEquals("nomeAtualizado", resultado.getNome());
    }

    @Test
    @DisplayName("Deve retornar uma página vazia quando não houver gerações ativas")
    void deveRetornarPaginaVaziaQuandoNaoHouverDados() {
        Pageable pagina = PageRequest.of(0, 10);
        Page<Geracao> paginaGeracaoVazia = new PageImpl<>(List.of(), pagina, 0);

        when(repository.findAllByAtivoTrue(pagina)).thenReturn(paginaGeracaoVazia);

        var resultado = service.listarPaginas(pagina);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        assertEquals(0, resultado.getContent().size());
    }

    @Test
    @DisplayName("Não Deve encontrar Geração pelo id")
    void naoDeveDeEncontrarGeracaoPeloId(){
        when(repository.findByIdAndAtivoTrue(999L)).thenReturn(Optional.empty());

        assertThrows(InvalidoException.class, () -> service.encontrarGeracao(999L));
    }

    @Test
    @DisplayName("Deve lançar InvalidoException ao tentar atualizar a geração inexistente")
    void deveLancarExcecaoAoAtualizarGeracaoInexistente(){
        var idInvalido = 999L;
        var dto = new GeracaoAtualizar("nomeAtualizado", 1, "Fato histórico atualizado",
                GeracaoCronologia.PIONEIROS_OU_VINTAGE, GeracaoEmpresaDominante.ATARI_DOMINANTE,
                new Periodo(LocalDate.now(), LocalDate.now()), false, true);

        when(repository.findByIdAndAtivoTrue(idInvalido)).thenReturn(Optional.empty());

        assertThrows(InvalidoException.class, () -> service.atualizarGeracao(idInvalido, dto));
        verify(repository, never()).save(any(Geracao.class));
    }

    @Test
    @DisplayName("Lança InvalidoException ao buscar slug inexistente")
    void deveLancarExcecaoAoBuscarSlugInexistente(){
        var slug = "naoExiste";
        when(repository.findBySlugIgnoreCaseAndAtivoTrue(slug)).thenReturn(Optional.empty());

        assertThrows(InvalidoException.class, () -> service.encontrarGeracaoPorSlug(slug));
    }

    @Test
    @DisplayName("Lança DataAccessException quando o bd estiver fora do ar")
    void deveLancarErroDeInfraestruturaQuando_O_BdCair(){
        var slug = "";
        when(repository.findBySlugIgnoreCaseAndAtivoTrue(slug))
                .thenThrow(new ConcurrencyFailureException("Erro simulado"));
        assertThrows(DataAccessException.class, () -> service.encontrarGeracaoPorSlug(slug));
    }

    @Test
    @DisplayName("Lança InvalidoException ao não ter estratégia de exclusão encontrada")
    void deveLancarInvalidoExceptionQuandoEstrategiaNaoForEncontrada(){
        var id = 1L;
        var logico = true;
        var logico2 = false;
        // tanto o logico quanto o logico2 deram certo

        assertThrows(InvalidoException.class, () -> service.deletarGeracao(id, logico2));
    }

    @Test
    @DisplayName("Lança NullPointerException apagando a estratégia de exclusão mockada lá de cima")
    void deveLancarNullPointerExceptionQuandoEstrategiaNaoForEncontrada(){
        var id = 1L;
        var logico = true;
        var logico2 = false;
        // se apagar o mock do geracaoexcluir esse teste passa

        assertThrows(NullPointerException.class, () -> service.deletarGeracao(id, logico2));
    }

}