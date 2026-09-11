package br.com.chupinvet.chupinvet.controller;

import br.com.chupinvet.chupinvet.dto.DiarioRequestDTO;
import br.com.chupinvet.chupinvet.dto.DiarioResponseDTO;
import br.com.chupinvet.chupinvet.service.DiarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/diarios")
@Tag(name = "Diário", description = "Endpoints para registro de diário dos pets")
public class DiarioController {

    @Autowired
    private DiarioService diarioService;

    @PostMapping
    @PreAuthorize("hasRole('RESPONSAVEL')")
    @Operation(
            summary = "Registrar diário",
            description = "Cria um novo registro de diário para um pet do responsável autenticado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "O pet informado não pertence ao responsável autenticado"),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado")
    })
    public ResponseEntity<DiarioResponseDTO> cadastrar(
            @RequestBody @Valid DiarioRequestDTO dto
    ) {
        DiarioResponseDTO diario = diarioService.cadastrar(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(diario);
    }

    @GetMapping("/pet/{idPet}")
    @Operation(
            summary = "Listar diário de um pet",
            description = "Retorna, paginado, o histórico de diário de um pet (mais recente primeiro)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso"),
            @ApiResponse(responseCode = "403", description = "O pet informado não pertence ao responsável autenticado"),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado")
    })
    public ResponseEntity<Page<DiarioResponseDTO>> listarPorPet(
            @PathVariable Long idPet,
            Pageable pageable
    ) {
        return ResponseEntity.ok(diarioService.listarPorPet(idPet, pageable));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar registro de diário por ID",
            description = "Retorna um registro específico de diário"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro encontrado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Registro não pertence ao responsável autenticado"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    })
    public ResponseEntity<DiarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(diarioService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RESPONSAVEL')")
    @Operation(
            summary = "Atualizar registro de diário",
            description = "Atualiza os dados de um registro de diário existente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro atualizado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Registro não pertence ao responsável autenticado"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<DiarioResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid DiarioRequestDTO dto
    ) {
        return ResponseEntity.ok(diarioService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RESPONSAVEL')")
    @Operation(
            summary = "Deletar registro de diário",
            description = "Remove um registro de diário pelo ID informado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro deletado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Registro não pertence ao responsável autenticado"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    })
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        diarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}