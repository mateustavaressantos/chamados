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

import br.com.sistemas.chamados.dto.ClienteRequest;
import br.com.sistemas.chamados.entity.Cliente;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.repository.ChamadoRepository;
import br.com.sistemas.chamados.repository.ClienteRepository;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @InjectMocks
    private ClienteService service;

    @Mock
    private ClienteRepository repository;

    @Mock
    private ChamadoRepository chamadoRepository;

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao tentar salvar e-mail que já existe")
    void deveLancarExcecaoQuandoEmailDuplicado() {
        ClienteRequest request = new ClienteRequest("Mateus", "mateus@gmail.com", "47999999999");
        
        when(repository.existsByEmail("mateus@gmail.com")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Já existe");

        verify(repository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao excluir cliente que possui chamados")
    void naoDeveExcluirQuandoPossuiChamados() {
        Cliente existente = new Cliente();
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(chamadoRepository.existsByClienteId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.excluir(1L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("possui chamados");

        verify(repository, never()).delete(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve excluir cliente que não possui chamados")
    void deveExcluirQuandoNaoPossuiChamados() {
        Cliente existente = new Cliente();
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(chamadoRepository.existsByClienteId(1L)).thenReturn(false);

        service.excluir(1L);

        verify(repository).delete(existente);
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao excluir cliente inexistente")
    void naoDeveExcluirInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.excluir(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(repository, never()).delete(any(Cliente.class));
    }
}