package historia_consoles.backend_Consoles.core.controller;

import historia_consoles.backend_Consoles.core.dto.console.ConsoleAtualizar;
import historia_consoles.backend_Consoles.core.dto.console.ConsoleCriar;
import historia_consoles.backend_Consoles.core.dto.console.ConsoleDetalhar;
import historia_consoles.backend_Consoles.core.dto.console.ConsoleListagem;
import historia_consoles.backend_Consoles.core.service.ConsoleService;
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
@RequestMapping("/console")
@RequiredArgsConstructor
@Tag(name = "Consoles", description = "Endpoints para gerenciamento e cadastro de hardwares de videogames")
@Slf4j
public class ConsoleController {

    private final ConsoleService service;

    @GetMapping
    @Operation(summary = "Listar consoles por geração", description = "Filtra e retorna todos os hardwares pertencentes a uma geração.")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponse(responseCode = "200", description = "Consoles listados.")
    @PageableAsQueryParam
    public ResponseEntity<Page<ConsoleListagem>> listarConsolesPorGeracao(
            @RequestParam("geracaoId") @Parameter(description = "ID da geração pai", example = "3") Long geracaoId,
            @PageableDefault(sort = "id") @Parameter(hidden = true) Pageable paginacao) {
        log.info("Requisição GET recebida em /console para listar hardwares da geração ID: {}", geracaoId);
        var consoles = service.listarPorGeracao(geracaoId, paginacao);
        return ResponseEntity.ok(consoles);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar console por ID")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Encontrado."),
            @ApiResponse(responseCode = "404", description = "Inexistente.")
    })
    public ResponseEntity<ConsoleDetalhar> listarConsole(@PathVariable @Parameter(description = "ID do console", example = "2") Long id) {
        log.info("Requisição GET recebida em /console/{}", id);
        var console = service.encontrarConsole(id);
        return ResponseEntity.ok(new ConsoleDetalhar(console));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Buscar console por Slug")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponse(responseCode = "200", description = "Encontrado.")
    public ResponseEntity<ConsoleDetalhar> buscarPorSlug(@PathVariable @Parameter(description = "Slug do console", example = "super-nintendo") String slug) {
        log.info("Requisição GET recebida em /console/slug/{}", slug);
        var console = service.encontrarConsolePorSlug(slug);
        return ResponseEntity.ok(new ConsoleDetalhar(console));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo console", description = "Adiciona um hardware. Requer ADMIN ou CARGO_CONSOLE.")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponse(responseCode = "201", description = "Criado com sucesso.")
    public ResponseEntity<ConsoleListagem> criar(@RequestBody @Valid ConsoleCriar dados, UriComponentsBuilder uri) {
        log.info("Requisição POST recebida em /console para criar o hardware: '{}'", dados.nome());
        var novoConsole = service.criar(dados);
        var path = uri.path("/console/{id}").buildAndExpand(novoConsole.getId()).toUri();
        return ResponseEntity.created(path).body(new ConsoleListagem(novoConsole));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados de um console")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponse(responseCode = "200", description = "Salvo.")
    public ResponseEntity<ConsoleListagem> atualizar(@PathVariable Long id, @RequestBody @Valid ConsoleAtualizar dados) {
        log.info("Requisição PUT recebida em /console/{} para modificação", id);
        var consoleAtualizado = service.atualizar(id, dados);
        return ResponseEntity.ok(new ConsoleListagem(consoleAtualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar ou inativar console")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponse(responseCode = "204", description = "Processado.")
    public ResponseEntity<Void> deletar(@PathVariable Long id, @RequestParam(defaultValue = "true") boolean logico) {
        log.info("Requisição DELETE recebida em /console/{} - Modo lógico: {}", id, logico);
        service.processarExclusao(id, logico);
        return ResponseEntity.noContent().build();
    }

}
