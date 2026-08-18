package historia_consoles.backend_Consoles.core.controller;

import historia_consoles.backend_Consoles.core.dto.jogo.JogoAtualizar;
import historia_consoles.backend_Consoles.core.dto.jogo.JogoCriar;
import historia_consoles.backend_Consoles.core.dto.jogo.JogoDetalhar;
import historia_consoles.backend_Consoles.core.dto.jogo.JogoListagem;
import historia_consoles.backend_Consoles.core.service.JogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/jogo")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Jogos", description = "Endpoints para gerenciamento do catálogo de títulos de videogames")
public class JogoController {

    private final JogoService service;

    private String getUsuarioLogado(){
        var autenticado = SecurityContextHolder.getContext().getAuthentication();
        if (autenticado != null && autenticado.isAuthenticated()){
            return autenticado.getName();
        }
        return "ANÔNIMO";
    }

    @GetMapping
    @Operation(summary = "Listar jogos de um console", description = "Retorna uma lista paginada de todos os jogos associados a um console específico. Requer autenticação (LEITOR).")
    @SecurityRequirement(name = "BearerAuth")
    @PageableAsQueryParam
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listagem recuperada com sucesso."),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido.")
    })
    public ResponseEntity<Page<JogoListagem>> listarJogosPorConsole(
            @RequestParam("consoleId") @Parameter(description = "ID do console para filtrar os jogos", example = "1") Long consoleId,
            @PageableDefault(sort = "id") @Parameter(hidden = true) Pageable paginacao) {
        log.debug("Requisição recebida para listar jogos do console ID: {}", consoleId);
        var jogos = service.listarPorConsole(consoleId, paginacao);
        return ResponseEntity.ok(jogos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar jogo por ID", description = "Recupera os detalhes completos de um jogo utilizando o seu identificador numérico.")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jogo localizado com sucesso."),
            @ApiResponse(responseCode = "401", description = "Não autorizado."),
            @ApiResponse(responseCode = "404", description = "Jogo não encontrado.")
    })
    public ResponseEntity<JogoDetalhar> listarJogo (@PathVariable @Parameter(description = "ID do jogo", example = "5") Long id) {
        log.debug("Requisição recebida para buscar jogo ID: {}", id);
        var jogos = service.encontrarJogo(id);
        return ResponseEntity.ok(jogos);
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Buscar jogo por Slug", description = "Busca uma obra utilizando a sua string de URL simplificada (slug).")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jogo localizado com sucesso."),
            @ApiResponse(responseCode = "401", description = "Não autorizado."),
            @ApiResponse(responseCode = "404", description = "Slug não encontrado.")
    })
    public ResponseEntity<JogoDetalhar> buscarPorSlug(@PathVariable @Parameter(description = "Slug do jogo", example = "super-mario-world") String slug) {
        log.debug("Requisição recebida para buscar jogo pelo slug: {}", slug);
        var jogo = service.encontrarJogoPorSlug(slug);
        return ResponseEntity.ok(jogo);
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo jogo", description = "Insere um novo título no sistema. Requer cargos elevados (ADMIN ou CARGO_JOGO).")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Jogo cadastrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados fornecidos falharam na validação."),
            @ApiResponse(responseCode = "401", description = "Não autorizado."),
            @ApiResponse(responseCode = "403", description = "Acesso negado para o cargo atual.")
    })
    public ResponseEntity<JogoListagem> criar(@RequestBody @Valid JogoCriar jogo, UriComponentsBuilder uri) {
        log.info("Requisição recebida para criar o jogo ID: {}", jogo.nome());
        var novoJogo = service.criar(jogo);
        var path = uri.path("/jogo/{id}").buildAndExpand(novoJogo.getId()).toUri();
        return ResponseEntity.created(path).body(new JogoListagem(novoJogo));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar jogo existente", description = "Modifica os dados cadastrais de um jogo. Requer privilégios de escrita.")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jogo atualizado com sucesso."),
            @ApiResponse(responseCode = "401", description = "Não autorizado."),
            @ApiResponse(responseCode = "403", description = "Permissão negada."),
            @ApiResponse(responseCode = "404", description = "Jogo não encontrado.")
    })
    public ResponseEntity<JogoListagem> atualizar(@PathVariable @Parameter(description = "ID do jogo a ser atualizado") Long id, @RequestBody @Valid JogoAtualizar dados) {
        log.info("Requisição recebida para atualizar o jogo ID: {}", id);
        var jogoAtualizado = service.atualizar(id, dados);
        return ResponseEntity.ok(new JogoListagem(jogoAtualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover jogo", description = "Executa a exclusão de um título, aceitando o parâmetro de deleção lógica (soft delete).")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Exclusão processada com sucesso."),
            @ApiResponse(responseCode = "401", description = "Não autorizado."),
            @ApiResponse(responseCode = "403", description = "Permissão negada.")
    })
    public ResponseEntity<Void> deletar(@PathVariable @Parameter(description = "ID do jogo a ser deletado") Long id, @RequestParam @Parameter(description = "Se true, inativa o registro sem apagar fisicamente", example = "true") boolean logico) {
        log.info("Requisição recebida para exclusão do jogo ID: {}. Tipo exclusão lógica: {}", id, logico);
        service.processarExclusao(id, logico);
        return ResponseEntity.noContent().build();
    }

}
