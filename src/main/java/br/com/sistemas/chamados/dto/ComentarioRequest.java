package br.com.sistemas.chamados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ComentarioRequest(
        @NotBlank(message = "O texto do comentário é obrigatório")
        @Size(max = 1000, message = "Máximo de 1000 caracteres")
        String texto,

        @NotBlank(message = "O nome do autor é obrigatório")
        String autor
) {}