package br.com.sistemas.chamados.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.sistemas.chamados.dto.ComentarioRequest;
import br.com.sistemas.chamados.entity.Chamado;
import br.com.sistemas.chamados.entity.Comentario;
import br.com.sistemas.chamados.entity.StatusChamado;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.repository.ChamadoRepository;
import br.com.sistemas.chamados.repository.ComentarioRepository;

@ExtendWith(MockitoExtension.class)
class ComentarioServiceTest {

    @InjectMocks
    private ComentarioService service;

    @Mock
    private ComentarioRepository comentarioRepository;

    @Mock
    private ChamadoRepository chamadoRepository;

    @Test
    @DisplayName("Deve lancar excecao ao tentar adicionar comentario em um chamado fechado")
    void deveLancarExcecaoAoComentarChamadoFechado() {
        Chamado chamado = new Chamado();
        chamado.setStatus(StatusChamado.FECHADO);

        when(chamadoRepository.findById(1L)).thenReturn(Optional.of(chamado));

        ComentarioRequest request = new ComentarioRequest("Texto de teste", "Sistema");

        assertThatThrownBy(() -> service.adicionar(1L, request))
                .isInstanceOf(RegraNegocioException.class); 

        verify(comentarioRepository, never()).save(any(Comentario.class));
    }
}