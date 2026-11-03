package br.com.sistemas.chamados.config;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import br.com.sistemas.chamados.entity.Categoria;
import br.com.sistemas.chamados.entity.Chamado;
import br.com.sistemas.chamados.entity.Cliente;
import br.com.sistemas.chamados.entity.Comentario;
import br.com.sistemas.chamados.entity.Perfil;
import br.com.sistemas.chamados.entity.Prioridade;
import br.com.sistemas.chamados.entity.StatusChamado;
import br.com.sistemas.chamados.entity.Tecnico;
import br.com.sistemas.chamados.repository.CategoriaRepository;
import br.com.sistemas.chamados.repository.ChamadoRepository;
import br.com.sistemas.chamados.repository.ClienteRepository;
import br.com.sistemas.chamados.repository.ComentarioRepository;
import br.com.sistemas.chamados.repository.TecnicoRepository;

@Configuration
public class TestConfig implements CommandLineRunner {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Autowired
    private ChamadoRepository chamadoRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Override
    public void run(String... args) throws Exception {
        Categoria cat1 = new Categoria();
        cat1.setNome("Hardware");
        cat1.setDescricao("Problemas com equipamentos físicos");

        Categoria cat2 = new Categoria();
        cat2.setNome("Redes");
        cat2.setDescricao("Problemas de conexão e internet");

        Categoria cat3 = new Categoria();
        cat3.setNome("Software");
        cat3.setDescricao("Problemas com sistemas e aplicativos");
        
        categoriaRepository.saveAll(Arrays.asList(cat1, cat2, cat3));

        Cliente cli1 = new Cliente();
        cli1.setNome("Empresa Para-Chamados");
        cli1.setEmail("email@pc.com");
        cli1.setTelefone("47999999999");

        Cliente cli2 = new Cliente();
        cli2.setNome("Loja de Jogos");
        cli2.setEmail("jogos@loja.com");
        cli2.setTelefone("4799999999");
        
        clienteRepository.saveAll(Arrays.asList(cli1, cli2));

        Tecnico tec1 = new Tecnico();
        tec1.setNome("Mateus Tavares");
        tec1.setEmail("mateus@admin.com");
        tec1.setPerfil(Perfil.ADMIN);

        Tecnico tec2 = new Tecnico();
        tec2.setNome("Valeska Ingrid");
        tec2.setEmail("valeska@tecnico.com");
        tec2.setPerfil(Perfil.TECNICO);
        
        tecnicoRepository.saveAll(Arrays.asList(tec1, tec2));

        Chamado ch1 = new Chamado();
        ch1.setTitulo("Computador não liga");
        ch1.setDescricao("O PC da recepção fez um barulho estranho e desligou.");
        ch1.setPrioridade(Prioridade.ALTA);
        ch1.setStatus(StatusChamado.ABERTO);
        ch1.setDataAbertura(LocalDateTime.now().minusDays(1));
        ch1.setCliente(cli1);
        ch1.setCategoria(cat1);

        Chamado ch2 = new Chamado();
        ch2.setTitulo("Internet lenta");
        ch2.setDescricao("A conexão está caindo toda a hora!");
        ch2.setPrioridade(Prioridade.MEDIA);
        ch2.setStatus(StatusChamado.EM_ATENDIMENTO);
        ch2.setDataAbertura(LocalDateTime.now().minusHours(5));
        ch2.setCliente(cli2);
        ch2.setCategoria(cat2);
        ch2.setTecnico(tec2);

        chamadoRepository.saveAll(Arrays.asList(ch1, ch2));

        Comentario com1 = new Comentario("Estou a caminho para verificar o roteador.", tec2.getNome(), ch2);
        comentarioRepository.save(com1);

        System.out.println("DADOS DE DEMONSTRAÇÃO CARREGADOS COM SUCESSO!");
        System.out.println("Categorias: " + categoriaRepository.count());
        System.out.println("Clientes: " + clienteRepository.count());
        System.out.println("Técnicos: " + tecnicoRepository.count());
        System.out.println("Chamados: " + chamadoRepository.count());
    }
}