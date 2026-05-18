package br.com.chupinvet.chupinvet.controller;

import br.com.chupinvet.chupinvet.dto.VeterinarioRequestDTO;
import br.com.chupinvet.chupinvet.dto.VeterinarioResponseDTO;
import br.com.chupinvet.chupinvet.service.VeterinarioService;
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
@RequestMapping("/veterinarios")
@Tag(name = "Veterinários", description = "Endpoints para gerenciamento de veterinários")
public class VeterinarioController {

    @Autowired
    private VeterinarioService veterinarioService;

    @PostMapping
    @Operation(
            summary = "Cadastrar veterinário",
            description = "Cadastra um novo veterinário no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Veterinário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<VeterinarioResponseDTO> cadastrar(
            @RequestBody @Valid VeterinarioRequestDTO dto
    ) {
        VeterinarioResponseDTO veterinario = veterinarioService.cadastrar(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(veterinario);
    }

    @GetMapping
    @Operation(
            summary = "Listar veterinários",
            description = "Retorna uma lista paginada de veterinários cadastrados"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de veterinários retornada com sucesso")
    })
    public ResponseEntity<Page<VeterinarioResponseDTO>> listar(Pageable pageable) {
        return ResponseEntity.ok(veterinarioService.listar(pageable));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar veterinário por ID",
            description = "Retorna um veterinário específico baseado no ID informado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Veterinário encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Veterinário não encontrado")
    })
    public ResponseEntity<VeterinarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(veterinarioService.buscarPorId(id));
    }

    @GetMapping("/especialidade")
    @Operation(
            summary = "Buscar veterinários por especialidade",
            description = "Retorna veterinários de acordo com a especialidade informada"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca por especialidade realizada com sucesso")
    })
    public ResponseEntity<Page<VeterinarioResponseDTO>> buscarPorEspecialidade(
            @RequestParam String especialidade,
            Pageable pageable
    ) {
        return ResponseEntity.ok(veterinarioService.buscarPorEspecialidade(especialidade, pageable));
    }

    @GetMapping("/servico")
    @Operation(
            summary = "Buscar veterinários por tipo de serviço",
            description = "Retorna veterinários de acordo com o tipo de serviço informado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca por tipo de serviço realizada com sucesso")
    })
    public ResponseEntity<Page<VeterinarioResponseDTO>> buscarPorTipoServico(
            @RequestParam String tipoServico,
            Pageable pageable
    ) {
        return ResponseEntity.ok(veterinarioService.buscarPorTipoServico(tipoServico, pageable));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar veterinário",
            description = "Atualiza os dados de um veterinário existente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Veterinário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Veterinário não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<VeterinarioResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid VeterinarioRequestDTO dto
    ) {
        return ResponseEntity.ok(veterinarioService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar veterinário",
            description = "Remove um veterinário do sistema pelo ID informado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Veterinário deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Veterinário não encontrado")
    })
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        veterinarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}