package br.com.sistemas.chamados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoriaRequest (
    @NotBlank(message = "Nome da categoria é obrigatório")
    @Size (min = 2, max = 100, message = "Nome da categoria deve ter entre 2 e 100 caracteres")
    @Pattern(regexp = Entrada.NOME_GERAL, message = "Nome da categoria contém caracteres inválidos")
    String nome,

    @NotBlank (message = "Descrição é obrigatória")
    @Size (min = 5, max = 300, message = "Descrição deve ter entre 5 e 300 caracteres")
    @Pattern(regexp = Entrada.TEXTO_LIVRE, message = "Descrição não pode conter < ou >")
    String descricao
){
    public CategoriaRequest {
        nome = Entrada.texto(nome);
        descricao = Entrada.texto(descricao);
    }
}
