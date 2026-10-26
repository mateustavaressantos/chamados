package br.com.sistemas.chamados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequest (
    @NotBlank(message = "Nome da categoria é obrigatório")
    @Size (max = 100, message = "Nome da categoria deve ter no máximo 100 caracteres")
    String nome,

    @NotBlank (message = "Descrição é obrigatória")
    @Size (max = 300, message = "Descrição deve ter no máximo 300 caracteres")
    String descricao
){}