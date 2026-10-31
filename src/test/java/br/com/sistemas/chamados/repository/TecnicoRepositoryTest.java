package br.com.sistemas.chamados.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import br.com.sistemas.chamados.entity.Tecnico;
import br.com.sistemas.chamados.entity.Perfil;

@DataJpaTest
class TecnicoRepositoryTest {

    @Autowired
    private TecnicoRepository repository;

    @Test
    @DisplayName("Deve retornar true quando o email do tecnico ja existe")
    void deveRetornarTrueQuandoEmailExiste() {
        Tecnico tecnico = new Tecnico();
        tecnico.setNome("Emilly");
        tecnico.setEmail("emilly@gmail.com");
        tecnico.setPerfil(Perfil.TECNICO);
        repository.save(tecnico);

        boolean existe = repository.existsByEmail("emilly@gmail.com");

        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando o email do tecnico nao existe")
    void deveRetornarFalseQuandoEmailNaoExiste() {
        boolean existe = repository.existsByEmail("novo@gmail.com");

        assertThat(existe).isFalse();
    }
}