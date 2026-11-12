package br.com.sistemas.chamados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteRequest (
    @NotBlank(message = "Nome é obrigatório")
    @Size (min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Pattern(regexp = Entrada.NOME_GERAL, message = "Nome contém caracteres inválidos")
    String nome,

    @NotBlank (message = "Email é obrigatório")
    @Size (max = 120, message = "Email deve ter no máximo 120 caracteres")
    @Pattern(regexp = Entrada.EMAIL, message = "Email inválido")
    String email,

    @Pattern(regexp = Entrada.TELEFONE, message = "Telefone inválido. Use DDD + número, ex.: (47) 99999-9999")
    String telefone
){
    public ClienteRequest {
        nome = Entrada.texto(nome);
        email = Entrada.email(email);
        telefone = Entrada.telefone(telefone);
    }
}
