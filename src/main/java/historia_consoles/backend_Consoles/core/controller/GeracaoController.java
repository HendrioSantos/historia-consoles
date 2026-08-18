package historia_consoles.backend_Consoles.core.controller;

import historia_consoles.backend_Consoles.core.dto.geracao.GeracaoAtualizar;
import historia_consoles.backend_Consoles.core.dto.geracao.GeracaoCriar;
import historia_consoles.backend_Consoles.core.dto.geracao.GeracaoListagem;
import historia_consoles.backend_Consoles.core.service.GeracaoService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/geracao")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Gerações", description = "Endpoints para catalogar as eras temporais da história dos videogames")
public class GeracaoController {

    private final GeracaoService service;

    @GetMapping
    @Operation(summary = "Listar gerações paginadas", description = "Retorna um bloco contendo as gerações de consoles registradas na linha do tempo.")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponse(responseCode = "200", description = "Gerações carregadas.")
    @PageableAsQueryParam
    public ResponseEntity<Page<GeracaoListagem>> listar(@PageableDefault(size = 10, sort = "id") @Parameter(hidden = true) Pageable paginacao) {
        log.info("Requisição GET recebida em /geracao - Página: {}, Tamanho: {}", paginacao.getPageNumber(), paginacao.getPageSize());
        var geracao = service.listarPaginas(paginacao);
        return ResponseEntity.ok(geracao);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar geração por ID")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sucesso."),
            @ApiResponse(responseCode = "404", description = "Geração não encontrada.")
    })
    public ResponseEntity<GeracaoListagem> listarGeracao(@PathVariable @Parameter(description = "ID numérico da geração", example = "4") Long id) {
        log.info("Requisição GET recebida em /geracao/{}", id);
        var geracao = service.encontrarGeracao(id);
        return ResponseEntity.ok(new GeracaoListagem(geracao));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Buscar geração por Slug")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponse(responseCode = "200", description = "Sucesso.")
    public ResponseEntity<GeracaoListagem> buscarPorSlug(@PathVariable @Parameter(description = "Slug da era", example = "quarta-geracao") String slug) {
        log.info("Requisição GET recebida em /geracao/slug/{}", slug);
        var geracao = service.encontrarGeracaoPorSlug(slug);
        return ResponseEntity.ok(new GeracaoListagem(geracao));
    }

    @PostMapping
    @Operation(summary = "Criar nova geração", description = "Insere uma nova era de hardware. Requer ADMIN ou CARGO_GERACAO.")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponse(responseCode = "201", description = "Geração salva.")
    public ResponseEntity<GeracaoListagem> criar(@RequestBody @Valid GeracaoCriar geracao, UriComponentsBuilder uri) {
        log.info("Requisição POST recebida em /geracao para criar: '{}'", geracao.nome());
        var novoGeracao = service.criarGeracao(geracao);
        var path = uri.path("/geracao/{id}").buildAndExpand(novoGeracao.getId()).toUri();
        return ResponseEntity.created(path).body(new GeracaoListagem(novoGeracao));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados de uma geração")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponse(responseCode = "200", description = "Modificações salvas.")
    public ResponseEntity<GeracaoListagem> atualizar(@PathVariable Long id, @RequestBody @Valid GeracaoAtualizar dados) {
        log.info("Requisição PUT recebida em /geracao/{} para atualização", id);
        var geracaoAtualizado = service.atualizarGeracao(id, dados);
        return ResponseEntity.ok(new GeracaoListagem(geracaoAtualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover geração")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponse(responseCode = "204", description = "Removido de forma limpa.")
    public ResponseEntity<Void> deletar(@PathVariable Long id, @RequestParam boolean logico) {
        log.info("Requisição DELETE recebida em /geracao/{} - Modo lógico: {}", id, logico);
        service.deletarGeracao(id, logico);
        return ResponseEntity.noContent().build();
    }

}
