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
import br.com.sistemas.chamados.entity.Comentario;
import br.com.sistemas.chamados.entity.Prioridade;
import br.com.sistemas.chamados.entity.StatusChamado;

@DataJpaTest
class ComentarioRepositoryTest {

    @Autowired private ComentarioRepository comentarioRepository;
    @Autowired private ChamadoRepository chamadoRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private CategoriaRepository categoriaRepository;

    @Test
    @DisplayName("Deve salvar um comentario associado a um chamado")
    void deveSalvarComentario() {
        Cliente cliente = new Cliente();
        cliente.setNome("Maria"); cliente.setEmail("maria@email.com");
        clienteRepository.save(cliente);

        Categoria categoria = new Categoria();
        categoria.setNome("Redes"); categoria.setDescricao("Falha de rede");
        categoriaRepository.save(categoria);

        Chamado chamado = new Chamado();
        chamado.setTitulo("Sem internet");
        chamado.setDescricao("Caiu tudo");
        chamado.setStatus(StatusChamado.ABERTO);
        chamado.setPrioridade(Prioridade.ALTA);
        chamado.setDataAbertura(LocalDateTime.now());
        chamado.setCliente(cliente);
        chamado.setCategoria(categoria);
        chamadoRepository.save(chamado);

        Comentario comentario = new Comentario("Verificando os cabos de rede.", "Sistema", chamado);

        Comentario salvo = comentarioRepository.save(comentario);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getChamado().getId()).isEqualTo(chamado.getId());
    }
}