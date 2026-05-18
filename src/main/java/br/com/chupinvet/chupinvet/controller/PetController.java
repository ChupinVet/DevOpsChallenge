package br.com.chupinvet.chupinvet.controller;

import br.com.chupinvet.chupinvet.dto.PetRequestDTO;
import br.com.chupinvet.chupinvet.dto.PetResponseDTO;
import br.com.chupinvet.chupinvet.service.PetService;
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
@RequestMapping("/pets")
@Tag(name = "Pets", description = "Endpoints para gerenciamento de pets")
public class PetController {

    @Autowired
    private PetService petService;

    @PostMapping
    @Operation(
            summary = "Cadastrar pet",
            description = "Cadastra um novo pet no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pet cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<PetResponseDTO> cadastrar(
            @RequestBody @Valid PetRequestDTO dto
    ) {
        PetResponseDTO pet = petService.cadastrar(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pet);
    }

    @GetMapping
    @Operation(
            summary = "Listar pets",
            description = "Retorna uma lista paginada de pets cadastrados"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pets retornada com sucesso")
    })
    public ResponseEntity<Page<PetResponseDTO>> listar(Pageable pageable) {

        return ResponseEntity.ok(petService.listar(pageable));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar pet por ID",
            description = "Retorna um pet específico baseado no ID informado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pet encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado")
    })
    public ResponseEntity<PetResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(petService.buscarPorId(id));
    }

    @GetMapping("/nome")
    @Operation(
            summary = "Buscar pets por nome",
            description = "Retorna pets cujo nome contenha o texto informado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca por nome realizada com sucesso")
    })
    public ResponseEntity<Page<PetResponseDTO>> buscarPorNome(
            @RequestParam String nomePet,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                petService.buscarPorNome(nomePet, pageable)
        );
    }

    @GetMapping("/especie")
    @Operation(
            summary = "Buscar pets por espécie",
            description = "Retorna pets de acordo com a espécie informada"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca por espécie realizada com sucesso")
    })
    public ResponseEntity<Page<PetResponseDTO>> buscarPorEspecie(
            @RequestParam String especie,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                petService.buscarPorEspecie(especie, pageable)
        );
    }

    @GetMapping("/raca")
    @Operation(
            summary = "Buscar pets por raça",
            description = "Retorna pets de acordo com a raça informada"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca por raça realizada com sucesso")
    })
    public ResponseEntity<Page<PetResponseDTO>> buscarPorRaca(
            @RequestParam String raca,
            Pageable pageable
    ) {
        return ResponseEntity.ok(petService.buscarPorRaca(raca, pageable));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar pet",
            description = "Atualiza os dados de um pet existente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pet atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<PetResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid PetRequestDTO dto
    ) {

        return ResponseEntity.ok(
                petService.atualizar(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar pet",
            description = "Remove um pet do sistema pelo ID informado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pet deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado")
    })
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        petService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}