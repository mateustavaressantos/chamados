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

import br.com.sistemas.chamados.dto.CategoriaRequest;
import br.com.sistemas.chamados.entity.Categoria;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.repository.CategoriaRepository;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @InjectMocks
    private CategoriaService service;

    @Mock
    private CategoriaRepository repository;

    @Test
    @DisplayName("Deve lancar RegraNegocioException ao tentar salvar categoria com nome que ja existe")
    void deveLancarExcecaoQuandoNomeDuplicado() {
        CategoriaRequest request = new CategoriaRequest("Hardware", "Problemas fisicos");
        
        when(repository.existsByNome("Hardware")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(RegraNegocioException.class);

        verify(repository, never()).save(any(Categoria.class));
    }
}