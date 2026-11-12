package br.com.sistemas.chamados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ComentarioRequest(
        @NotBlank(message = "O texto do comentário é obrigatório")
        @Size(max = 1000, message = "Máximo de 1000 caracteres")
        @Pattern(regexp = Entrada.TEXTO_LIVRE, message = "O comentário não pode conter < ou >")
        String texto,

        @NotBlank(message = "O nome do autor é obrigatório")
        @Size(min = 2, max = 100, message = "O nome do autor deve ter entre 2 e 100 caracteres")
        @Pattern(regexp = Entrada.NOME_PESSOA, message = "O nome do autor deve conter apenas letras, espaços, ponto, apóstrofo ou hífen")
        String autor
) {
    public ComentarioRequest {
        texto = Entrada.texto(texto);
        autor = Entrada.texto(autor);
    }
}
