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

import br.com.sistemas.chamados.dto.TecnicoRequest;
import br.com.sistemas.chamados.entity.Tecnico;
import br.com.sistemas.chamados.entity.Perfil;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.repository.ChamadoRepository;
import br.com.sistemas.chamados.repository.TecnicoRepository;

@ExtendWith(MockitoExtension.class)
class TecnicoServiceTest {

    @InjectMocks
    private TecnicoService service;

    @Mock
    private TecnicoRepository repository;

    @Mock
    private ChamadoRepository chamadoRepository;

    @Test
    @DisplayName("Deve lancar RegraNegocioException ao tentar salvar tecnico com email que ja existe")
    void deveLancarExcecaoQuandoEmailDuplicado() {
        TecnicoRequest request = new TecnicoRequest("Emilly", "emilly@gmail.com", Perfil.TECNICO);
        
        when(repository.existsByEmail("emilly@gmail.com")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(RegraNegocioException.class);

        verify(repository, never()).save(any(Tecnico.class));
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao excluir tecnico que possui chamados")
    void naoDeveExcluirQuandoPossuiChamados() {
        Tecnico existente = new Tecnico();
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(chamadoRepository.existsByTecnicoId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.excluir(1L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("atribuído a chamados");

        verify(repository, never()).delete(any(Tecnico.class));
    }

    @Test
    @DisplayName("Deve excluir tecnico que não possui chamados")
    void deveExcluirQuandoNaoPossuiChamados() {
        Tecnico existente = new Tecnico();
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(chamadoRepository.existsByTecnicoId(1L)).thenReturn(false);

        service.excluir(1L);

        verify(repository).delete(existente);
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao excluir tecnico inexistente")
    void naoDeveExcluirInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.excluir(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(repository, never()).delete(any(Tecnico.class));
    }
}