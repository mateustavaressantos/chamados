package br.com.sistemas.chamados.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import br.com.sistemas.chamados.entity.Cliente;

@DataJpaTest
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository repository;

    @Test
    @DisplayName("Deve retornar true quando o email já existe")
    void deveRetornarTrueQuandoEmailExiste() {
        Cliente cliente = new Cliente("Mateus Tavares", "mateus@gmail.com", "47999999999");
        repository.save(cliente);

        boolean existe = repository.existsByEmail("mateus@gmail.com");

        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando o email não existe")
    void deveRetornarFalseQuandoEmailNaoExiste() {
        boolean existe = repository.existsByEmail("novo@gmail.com");

        assertThat(existe).isFalse();
    }
}