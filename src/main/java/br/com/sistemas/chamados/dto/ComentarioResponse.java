package br.com.sistemas.chamados.dto;

import java.time.LocalDateTime;
import br.com.sistemas.chamados.entity.Comentario;

public record ComentarioResponse(Long id, String texto, String autor, LocalDateTime dataHora) {
    public static ComentarioResponse de(Comentario c) {
        return new ComentarioResponse(c.getId(), c.getTexto(), c.getAutor(), c.getDataHora());
    }
}