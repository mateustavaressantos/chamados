package br.com.sistemas.chamados.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.sistemas.chamados.dto.AlterarStatusRequest;
import br.com.sistemas.chamados.entity.Chamado;
import br.com.sistemas.chamados.entity.StatusChamado;
import br.com.sistemas.chamados.exception.ChamadoFechadoException;
import br.com.sistemas.chamados.repository.ChamadoRepository;

@ExtendWith(MockitoExtension.class)
class ChamadoServiceTest {

    @InjectMocks
    private ChamadoService service;

    @Mock
    private ChamadoRepository repository;

    @Test
    @DisplayName("Deve lancar excecao ao tentar alterar status de um chamado ja fechado")
    void deveLancarExcecaoQuandoChamadoFechado() {
        Chamado chamado = new Chamado();
        chamado.setStatus(StatusChamado.FECHADO);

        when(repository.findById(1L)).thenReturn(Optional.of(chamado));

        AlterarStatusRequest request = new AlterarStatusRequest(StatusChamado.EM_ATENDIMENTO);

        assertThatThrownBy(() -> service.alterarStatus(1L, request))
                .isInstanceOf(ChamadoFechadoException.class);
    }
}