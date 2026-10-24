package br.com.sistemas.chamados.dto;

import java.time.LocalDateTime;
import java.util.List;
import br.com.sistemas.chamados.entity.Chamado;

public record ChamadoResponse(
        Long id,
        String titulo,
        String descricao,
        String status,
        String prioridade,
        LocalDateTime dataAbertura,
        LocalDateTime dataFechamento,
        String nomeCliente,
        String nomeTecnico,
        List<ComentarioResponse> comentarios 
) {
    public static ChamadoResponse de(Chamado c) {
        
        List<ComentarioResponse> listaComentarios = null;
        if (c.getComentarios() != null) {
            listaComentarios = c.getComentarios().stream()
                    .map(ComentarioResponse::de)
                    .toList();
        }

        return new ChamadoResponse(
                c.getId(),
                c.getTitulo(),
                c.getDescricao(),
                c.getStatus().name(),
                c.getPrioridade().name(),
                c.getDataAbertura(),
                c.getDataFechamento(),
                c.getCliente().getNome(),
                c.getTecnico() != null ? c.getTecnico().getNome() : null,
                listaComentarios
        );
    }
}