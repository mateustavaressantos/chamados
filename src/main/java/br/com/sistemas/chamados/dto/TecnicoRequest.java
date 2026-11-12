package br.com.sistemas.chamados.dto;

import br.com.sistemas.chamados.entity.Perfil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TecnicoRequest (
    @NotBlank(message = "Nome é obrigatório")
    @Size (min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Pattern(regexp = Entrada.NOME_PESSOA, message = "Nome deve conter apenas letras, espaços, ponto, apóstrofo ou hífen")
    String nome,

    @NotBlank (message = "Email é obrigatório")
    @Size (max = 120, message = "Email deve ter no máximo 120 caracteres")
    @Pattern(regexp = Entrada.EMAIL, message = "Email inválido")
    String email,

    @NotNull(message = "Perfil é obrigatório (ADMIN ou TECNICO)")
    Perfil perfil
){
    public TecnicoRequest {
        nome = Entrada.texto(nome);
        email = Entrada.email(email);
    }
}
