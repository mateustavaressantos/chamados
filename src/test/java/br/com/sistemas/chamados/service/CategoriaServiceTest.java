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

import br.com.sistemas.chamados.dto.CategoriaRequest;
import br.com.sistemas.chamados.entity.Categoria;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.repository.ChamadoRepository;
import br.com.sistemas.chamados.repository.CategoriaRepository;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @InjectMocks
    private CategoriaService service;

    @Mock
    private CategoriaRepository repository;

    @Mock
    private ChamadoRepository chamadoRepository;

    @Test
    @DisplayName("Deve lancar RegraNegocioException ao tentar salvar categoria com nome que ja existe")
    void deveLancarExcecaoQuandoNomeDuplicado() {
        CategoriaRequest request = new CategoriaRequest("Hardware", "Problemas fisicos");
        
        when(repository.existsByNome("Hardware")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(RegraNegocioException.class);

        verify(repository, never()).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao excluir categoria que possui chamados")
    void naoDeveExcluirQuandoPossuiChamados() {
        Categoria existente = new Categoria();
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(chamadoRepository.existsByCategoriaId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.excluir(1L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("possui chamados");

        verify(repository, never()).delete(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve excluir categoria que não possui chamados")
    void deveExcluirQuandoNaoPossuiChamados() {
        Categoria existente = new Categoria();
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(chamadoRepository.existsByCategoriaId(1L)).thenReturn(false);

        service.excluir(1L);

        verify(repository).delete(existente);
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao excluir categoria inexistente")
    void naoDeveExcluirInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.excluir(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(repository, never()).delete(any(Categoria.class));
    }
}