package br.com.sistemas.chamados.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.sistemas.chamados.dto.ClienteRequest;
import br.com.sistemas.chamados.entity.Cliente;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.repository.ClienteRepository;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @InjectMocks
    private ClienteService service;

    @Mock
    private ClienteRepository repository;

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
}