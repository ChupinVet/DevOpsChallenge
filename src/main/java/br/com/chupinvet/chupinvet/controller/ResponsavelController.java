package br.com.chupinvet.chupinvet.controller;

import br.com.chupinvet.chupinvet.dto.ResponsavelRequestDTO;
import br.com.chupinvet.chupinvet.dto.ResponsavelResponseDTO;
import br.com.chupinvet.chupinvet.service.ResponsavelService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/responsaveis")
@Tag(name = "Responsáveis", description = "Endpoints para gerenciamento de responsáveis")
public class ResponsavelController {

    @Autowired
    private ResponsavelService responsavelService;

    @PostMapping
    @Operation(
            summary = "Cadastrar responsável",
            description = "Cadastra um novo responsável no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Responsável cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ResponsavelResponseDTO> cadastrar(
            @RequestBody @Valid ResponsavelRequestDTO dto
    ) {
        ResponsavelResponseDTO responsavel = responsavelService.cadastrar(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responsavel);
    }

    @GetMapping
    @Operation(
            summary = "Listar responsáveis",
            description = "Retorna uma lista paginada de responsáveis cadastrados"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de responsáveis retornada com sucesso")
    })
    public ResponseEntity<Page<ResponsavelResponseDTO>> listar(Pageable pageable) {
        return ResponseEntity.ok(responsavelService.listar(pageable));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar responsável por ID",
            description = "Retorna um responsável específico baseado no ID informado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Responsável encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Responsável não encontrado")
    })
    public ResponseEntity<ResponsavelResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(responsavelService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar responsável",
            description = "Atualiza os dados de um responsável existente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Responsável atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Responsável não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ResponsavelResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ResponsavelRequestDTO dto
    ) {
        return ResponseEntity.ok(
                responsavelService.atualizar(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar responsável",
            description = "Remove um responsável do sistema pelo ID informado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Responsável deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Responsável não encontrado")
    })
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        responsavelService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}