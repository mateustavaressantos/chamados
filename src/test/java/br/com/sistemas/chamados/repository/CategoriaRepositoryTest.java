package br.com.sistemas.chamados.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import br.com.sistemas.chamados.entity.Categoria;

@DataJpaTest
class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository repository;

    @Test
    @DisplayName("Deve retornar true quando o nome da categoria já existe")
    void deveRetornarTrueQuandoNomeExiste() {
        Categoria categoria = new Categoria();
        categoria.setNome("Hardware");
        categoria.setDescricao("Problemas físicos em equipamentos");
        repository.save(categoria);

        boolean existe = repository.existsByNome("Hardware");

        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando o nome da categoria não existe")
    void deveRetornarFalseQuandoNomeNaoExiste() {
        boolean existe = repository.existsByNome("Software");

        assertThat(existe).isFalse();
    }
}