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

import br.com.sistemas.chamados.dto.TecnicoRequest;
import br.com.sistemas.chamados.entity.Tecnico;
import br.com.sistemas.chamados.entity.Perfil;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.repository.TecnicoRepository;

@ExtendWith(MockitoExtension.class)
class TecnicoServiceTest {

    @InjectMocks
    private TecnicoService service;

    @Mock
    private TecnicoRepository repository;

    @Test
    @DisplayName("Deve lancar RegraNegocioException ao tentar salvar tecnico com email que ja existe")
    void deveLancarExcecaoQuandoEmailDuplicado() {
        TecnicoRequest request = new TecnicoRequest("Emilly", "emilly@gmail.com", Perfil.TECNICO);
        
        when(repository.existsByEmail("emilly@gmail.com")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(RegraNegocioException.class);

        verify(repository, never()).save(any(Tecnico.class));
    }
}