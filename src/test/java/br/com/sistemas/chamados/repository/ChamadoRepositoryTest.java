package br.com.sistemas.chamados.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import br.com.sistemas.chamados.entity.Categoria;
import br.com.sistemas.chamados.entity.Chamado;
import br.com.sistemas.chamados.entity.Cliente;
import br.com.sistemas.chamados.entity.Prioridade;
import br.com.sistemas.chamados.entity.StatusChamado;

@DataJpaTest
class ChamadoRepositoryTest {

    @Autowired
    private ChamadoRepository chamadoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    @DisplayName("Deve salvar um chamado associado a um cliente e a uma categoria")
    void deveSalvarChamado() {
        Cliente cliente = new Cliente();
        cliente.setNome("Joao");
        cliente.setEmail("joao@email.com");
        cliente.setTelefone("999999999");
        clienteRepository.save(cliente);

        Categoria categoria = new Categoria();
        categoria.setNome("Hardware");
        categoria.setDescricao("Problema de hardware");
        categoriaRepository.save(categoria);

        Chamado chamado = new Chamado();
        chamado.setTitulo("Problema no rato");
        chamado.setDescricao("O rato deixou de funcionar");
        chamado.setStatus(StatusChamado.ABERTO);
        chamado.setPrioridade(Prioridade.ALTA);
        chamado.setDataAbertura(LocalDateTime.now());
        
        chamado.setCliente(cliente);
        chamado.setCategoria(categoria);

        Chamado salvo = chamadoRepository.save(chamado);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getStatus()).isEqualTo(StatusChamado.ABERTO);
        assertThat(salvo.getCliente().getId()).isEqualTo(cliente.getId());
        assertThat(salvo.getCategoria().getId()).isEqualTo(categoria.getId());
    }
}