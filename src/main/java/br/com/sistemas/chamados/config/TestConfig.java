package br.com.sistemas.chamados.config;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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

        // ---------- Dados adicionais de demonstração ----------

        List<Categoria> novasCategorias = categoriaRepository.saveAll(Arrays.asList(
            categoria("Banco de Dados", "Falhas, lentidão e acesso a bancos de dados"),
            categoria("Impressoras", "Problemas com impressoras e digitalizadores"),
            categoria("E-mail", "Configuração e problemas de contas de e-mail"),
            categoria("Segurança", "Incidentes de segurança, vírus e acessos indevidos"),
            categoria("Acesso e Senhas", "Redefinição de senha e permissões de acesso"),
            categoria("Telefonia", "Ramais, telefones IP e chamadas"),
            categoria("Backup", "Rotinas de backup e restauração de dados"),
            categoria("Sistemas Internos", "Erros em sistemas desenvolvidos pela empresa"),
            categoria("Infraestrutura", "Servidores, energia e cabeamento"),
            categoria("Treinamento", "Dúvidas de uso e solicitações de capacitação")));

        List<Cliente> novosClientes = clienteRepository.saveAll(Arrays.asList(
            cliente("Padaria Pão Quente", "contato@paoquente.com", "47988887777"),
            cliente("Clínica Vida Saudável", "ti@vidasaudavel.com", "47977776666"),
            cliente("Escritório Silva & Associados", "suporte@silvaassociados.com", "47966665555"),
            cliente("Tech Solutions Ltda", "chamados@techsolutions.com", "47955554444"),
            cliente("Escola Aprender Mais", "secretaria@aprendermais.com", "47944443333")));

        List<Tecnico> novosTecnicos = tecnicoRepository.saveAll(Arrays.asList(
            tecnico("Carlos Eduardo", "carlos@tecnico.com", Perfil.TECNICO),
            tecnico("Ana Beatriz", "ana@admin.com", Perfil.ADMIN)));

        Tecnico carlos = novosTecnicos.get(0);
        Tecnico ana = novosTecnicos.get(1);

        Chamado c3 = chamado("Banco de dados fora do ar", "O sistema não conecta ao banco desde a manhã.",
            Prioridade.CRITICA, StatusChamado.EM_ATENDIMENTO, LocalDateTime.now().minusHours(3),
            novosClientes.get(3), novasCategorias.get(0), ana);
        Chamado c4 = chamado("Impressora não imprime", "A impressora do caixa exibe erro de papel atolado.",
            Prioridade.BAIXA, StatusChamado.ABERTO, LocalDateTime.now().minusDays(2),
            novosClientes.get(0), novasCategorias.get(1), null);
        Chamado c5 = chamado("Não recebo e-mails", "Caixa de entrada parou de receber mensagens ontem.",
            Prioridade.MEDIA, StatusChamado.AGUARDANDO_CLIENTE, LocalDateTime.now().minusDays(3),
            novosClientes.get(2), novasCategorias.get(2), carlos);
        Chamado c6 = chamado("Suspeita de vírus", "Computadores exibem janelas estranhas e estão lentos.",
            Prioridade.ALTA, StatusChamado.EM_ATENDIMENTO, LocalDateTime.now().minusHours(8),
            novosClientes.get(1), novasCategorias.get(3), ana);
        Chamado c7 = chamado("Esqueci minha senha", "Preciso redefinir a senha do sistema de prontuários.",
            Prioridade.MEDIA, StatusChamado.FECHADO, LocalDateTime.now().minusDays(5),
            novosClientes.get(1), novasCategorias.get(4), carlos);
        Chamado c8 = chamado("Ramal sem sinal", "O ramal da secretaria está mudo.",
            Prioridade.BAIXA, StatusChamado.ABERTO, LocalDateTime.now().minusHours(2),
            novosClientes.get(4), novasCategorias.get(5), null);
        Chamado c9 = chamado("Backup não concluiu", "A rotina noturna de backup falhou três dias seguidos.",
            Prioridade.ALTA, StatusChamado.ABERTO, LocalDateTime.now().minusHours(12),
            novosClientes.get(2), novasCategorias.get(6), null);
        Chamado c10 = chamado("Erro ao emitir relatório", "O sistema interno retorna erro 500 ao gerar o relatório mensal.",
            Prioridade.MEDIA, StatusChamado.EM_ATENDIMENTO, LocalDateTime.now().minusDays(1),
            novosClientes.get(3), novasCategorias.get(7), carlos);
        Chamado c11 = chamado("Servidor reiniciando sozinho", "O servidor de arquivos reinicia várias vezes ao dia.",
            Prioridade.CRITICA, StatusChamado.ABERTO, LocalDateTime.now().minusMinutes(45),
            novosClientes.get(4), novasCategorias.get(8), null);
        Chamado c12 = chamado("Treinamento do novo sistema", "Equipe precisa de capacitação para usar o novo sistema de vendas.",
            Prioridade.BAIXA, StatusChamado.FECHADO, LocalDateTime.now().minusDays(7),
            novosClientes.get(0), novasCategorias.get(9), ana);

        chamadoRepository.saveAll(Arrays.asList(c3, c4, c5, c6, c7, c8, c9, c10, c11, c12));

        comentarioRepository.saveAll(Arrays.asList(
            new Comentario("Verificando os logs do servidor de banco de dados.", ana.getNome(), c3),
            new Comentario("Aguardando o cliente enviar print do erro.", carlos.getNome(), c5),
            new Comentario("Antivírus executado, iniciando limpeza dos arquivos infectados.", ana.getNome(), c6),
            new Comentario("Senha redefinida com sucesso. Chamado encerrado.", carlos.getNome(), c7),
            new Comentario("Treinamento realizado com toda a equipe.", ana.getNome(), c12)));

        System.out.println("DADOS DE DEMONSTRAÇÃO CARREGADOS COM SUCESSO!");
        System.out.println("Categorias: " + categoriaRepository.count());
        System.out.println("Clientes: " + clienteRepository.count());
        System.out.println("Técnicos: " + tecnicoRepository.count());
        System.out.println("Chamados: " + chamadoRepository.count());
    }

    private Categoria categoria(String nome, String descricao) {
        Categoria c = new Categoria();
        c.setNome(nome);
        c.setDescricao(descricao);
        return c;
    }

    private Cliente cliente(String nome, String email, String telefone) {
        Cliente c = new Cliente();
        c.setNome(nome);
        c.setEmail(email);
        c.setTelefone(telefone);
        return c;
    }

    private Tecnico tecnico(String nome, String email, Perfil perfil) {
        Tecnico t = new Tecnico();
        t.setNome(nome);
        t.setEmail(email);
        t.setPerfil(perfil);
        return t;
    }

    private Chamado chamado(String titulo, String descricao, Prioridade prioridade, StatusChamado status,
                            LocalDateTime dataAbertura, Cliente cliente, Categoria categoria, Tecnico tecnico) {
        Chamado c = new Chamado();
        c.setTitulo(titulo);
        c.setDescricao(descricao);
        c.setPrioridade(prioridade);
        c.setStatus(status);
        c.setDataAbertura(dataAbertura);
        c.setCliente(cliente);
        c.setCategoria(categoria);
        c.setTecnico(tecnico);
        return c;
    }
}