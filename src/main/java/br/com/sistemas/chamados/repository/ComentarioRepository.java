package br.com.sistemas.chamados.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.sistemas.chamados.entity.Comentario;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    // Spring cria o SQL automaticamente: SELECT * FROM comentarios WHERE chamado_id = ? ORDER BY data_hora ASC
    List<Comentario> findByChamadoIdOrderByDataHoraAsc(Long chamadoId);
}