package br.com.sistemas.chamados.dto;

import br.com.sistemas.chamados.entity.Prioridade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ChamadoRequest(
        @NotBlank(message = "O título é obrigatório")
        @Size(min = 3, max = 150, message = "O título deve ter entre 3 e 150 caracteres")
        @Pattern(regexp = Entrada.TITULO, message = "O título contém caracteres inválidos")
        String titulo,

        @NotBlank(message = "A descrição é obrigatória")
        @Size(min = 5, max = 1000, message = "A descrição deve ter entre 5 e 1000 caracteres")
        @Pattern(regexp = Entrada.TEXTO_LIVRE, message = "A descrição não pode conter < ou >")
        String descricao,

        @NotNull(message = "A prioridade é obrigatória (BAIXA, MEDIA, ALTA ou CRITICA)")
        Prioridade prioridade,

        @NotNull(message = "O ID do cliente é obrigatório")
        @Positive(message = "O ID do cliente deve ser positivo")
        Long clienteId,

        @NotNull(message = "O ID da categoria é obrigatória")
        @Positive(message = "O ID da categoria deve ser positivo")
        Long categoriaId
) {
    public ChamadoRequest {
        titulo = Entrada.texto(titulo);
        descricao = Entrada.texto(descricao);
    }
}
