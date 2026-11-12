package br.com.sistemas.chamados.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import br.com.sistemas.chamados.dto.CategoriaRequest;
import br.com.sistemas.chamados.dto.ClienteRequest;
import br.com.sistemas.chamados.dto.TecnicoRequest;
import br.com.sistemas.chamados.entity.Perfil;
import br.com.sistemas.chamados.service.CategoriaService;
import br.com.sistemas.chamados.service.ClienteService;
import br.com.sistemas.chamados.service.TecnicoService;

@SpringBootTest
class PaginasWebTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private TecnicoService tecnicoService;

    private MockMvc mockMvc;

    @BeforeEach
    void preparar() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private void abre(String url, String... trechos) throws Exception {
        String html = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(html).contains(trechos);
    }

    @Test
    @DisplayName("Tela 1 - Dashboard")
    void dashboard() throws Exception {
        abre("/", "Dashboard", "Chamados por status", "Últimos chamados");
    }

    @Test
    @DisplayName("Tela 2 - Clientes")
    void clientes() throws Exception {
        abre("/web/clientes", "Novo cliente", "Loja de Jogos", "Empresa Para-Chamados", "47999999999");
    }

    @Test
    @DisplayName("Tela 3 - Lista de chamados")
    void listaDeChamados() throws Exception {
        abre("/web/chamados", "Internet lenta", "Computador não liga");
    }

    @Test
    @DisplayName("Tela 4 - Novo chamado")
    void novoChamado() throws Exception {
        abre("/web/chamados/novo", "Novo chamado", "Abrir chamado", "Loja de Jogos", "Hardware");
    }

    @Test
    @DisplayName("Tela 5 - Detalhe do chamado")
    void detalheDoChamado() throws Exception {
        abre("/web/chamados/2", "Internet lenta", "Comentários e histórico", "Atribuir técnico", "Alterar status");
    }

    @Test
    @DisplayName("Tela 6 - Categorias")
    void categorias() throws Exception {
        abre("/web/categorias", "Hardware", "Redes", "Software", "Nova categoria");
    }

    @Test
    @DisplayName("Tela 7 - Técnicos")
    void tecnicos() throws Exception {
        abre("/web/tecnicos", "Mateus Tavares", "Valeska Ingrid", "Novo técnico");
    }

    @Test
    @DisplayName("Tela 8 - Editar cliente (telefone vem preenchido)")
    void editarCliente() throws Exception {
        abre("/web/clientes/1/editar", "Editar cliente", "email@pc.com", "47999999999", "Salvar alterações");
    }

    @Test
    @DisplayName("Tela 9 - Chamados do cliente")
    void chamadosDoCliente() throws Exception {
        abre("/web/clientes/2/chamados", "Chamados de Loja de Jogos", "Internet lenta");
    }

    @Test
    @DisplayName("Tela 10 - Chamado fechado (formulários bloqueados)")
    void chamadoFechado() throws Exception {
        String id = abrirChamado();

        mockMvc.perform(post("/web/chamados/" + id + "/status").param("status", "FECHADO"))
                .andExpect(status().is3xxRedirection());

        abre("/web/chamados/" + id,
                "Este chamado já foi fechado e não pode ser alterado.",
                "Não é possível adicionar comentários a um chamado fechado.");
    }

    @Test
    @DisplayName("Tela 11 - Confirmar exclusão")
    void confirmarExclusao() throws Exception {
        abre("/web/clientes/1/excluir", "Deseja excluir este cliente?", "Excluir cliente");
    }

    @Test
    @DisplayName("Tela 12 - Lista sem resultados")
    void listaSemResultados() throws Exception {
        abre("/web/chamados?status=FECHADO&prioridade=CRITICA", "Nenhum chamado encontrado");
    }

    @Test
    @DisplayName("Tela 13 - Erro 404 (página em templates/error/404.html)")
    void erro404() throws Exception {
        String html = mockMvc.perform(get("/web/chamados/99999"))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(html).contains("404", "Chamado 99999 não encontrado");
    }

    @Test
    @DisplayName("Cadastrar cliente redireciona para a lista")
    void cadastrarCliente() throws Exception {
        mockMvc.perform(post("/web/clientes")
                        .param("nome", "Cliente Teste")
                        .param("email", UUID.randomUUID() + "@teste.com")
                        .param("telefone", "47999990000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("sucesso", "Cliente cadastrado."));
    }

    @Test
    @DisplayName("Cliente com dados inválidos volta ao formulário com a mensagem (validação)")
    void clienteInvalido() throws Exception {
        String html = mockMvc.perform(post("/web/clientes")
                        .param("nome", "")
                        .param("email", "email-invalido"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(html).contains("Nome é obrigatório");
    }

    @Test
    @DisplayName("Telefone com máscara é aceito e gravado só com dígitos")
    void clienteComTelefoneMascarado() throws Exception {
        String email = UUID.randomUUID() + "@teste.com";
        mockMvc.perform(post("/web/clientes")
                        .param("nome", "Cliente Mascara")
                        .param("email", email)
                        .param("telefone", "(47) 98888-1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("sucesso", "Cliente cadastrado."));
        assertThat(clienteService.listar())
                .anyMatch(c -> email.equals(c.email()) && "47988881234".equals(c.telefone()));
    }

    @Test
    @DisplayName("Telefone inválido e nome com HTML voltam ao formulário com mensagens")
    void clienteComTelefoneENomeInvalidos() throws Exception {
        String html = mockMvc.perform(post("/web/clientes")
                        .param("nome", "<script>alert(1)</script>")
                        .param("email", UUID.randomUUID() + "@teste.com")
                        .param("telefone", "123"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(html).contains("Nome contém caracteres inválidos", "Telefone inválido");
        assertThat(html).doesNotContain("<script>alert(1)</script>");
    }

    @Test
    @DisplayName("E-mail duplicado mostra o erro no formulário (regra de negócio)")
    void clienteComEmailDuplicado() throws Exception {
        String html = mockMvc.perform(post("/web/clientes")
                        .param("nome", "Outro Cliente")
                        .param("email", "email@pc.com")
                        .param("telefone", "47977777777"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(html).contains("Já existe cliente com o e-mail email@pc.com");
    }

    @Test
    @DisplayName("Comentar em chamado fechado mostra erro na tela")
    void comentarEmChamadoFechado() throws Exception {
        String id = abrirChamado();
        mockMvc.perform(post("/web/chamados/" + id + "/status").param("status", "FECHADO"));

        mockMvc.perform(post("/web/chamados/" + id + "/comentarios")
                        .param("autor", "Teste")
                        .param("texto", "Tentando comentar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("erro", "Não é possível adicionar comentários a um chamado fechado."));
    }

    @Test
    @DisplayName("Tela: não exclui cliente que possui chamados")
    void naoExcluiClienteComChamados() throws Exception {
        mockMvc.perform(post("/web/clientes/1/excluir"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("erro", "Não é possível excluir: este cliente possui chamados."));
    }

    @Test
    @DisplayName("Tela: não exclui categoria que possui chamados")
    void naoExcluiCategoriaComChamados() throws Exception {
        // A categoria 1 (Hardware) tem o chamado "Computador não liga"
        mockMvc.perform(post("/web/categorias/1/excluir"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("erro", "Não é possível excluir: esta categoria possui chamados."));
    }

    @Test
    @DisplayName("Tela: não exclui técnico atribuído a chamados")
    void naoExcluiTecnicoComChamados() throws Exception {
        // O técnico 2 (Valeska) está atribuído ao chamado "Internet lenta"
        mockMvc.perform(post("/web/tecnicos/2/excluir"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("erro", "Não é possível excluir: este técnico está atribuído a chamados."));
    }

    @Test
    @DisplayName("Tela: exclui cliente sem chamados")
    void excluiClienteSemChamados() throws Exception {
        Long id = clienteService.criar(new ClienteRequest("Sem Chamados", UUID.randomUUID() + "@teste.com", "4733330000")).id();

        mockMvc.perform(post("/web/clientes/" + id + "/excluir"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("sucesso", "Cliente excluído."));
    }

    @Test
    @DisplayName("Tela: exclui categoria sem chamados")
    void excluiCategoriaSemChamados() throws Exception {
        Long id = categoriaService.criar(new CategoriaRequest("Temp " + UUID.randomUUID(), "Sem uso")).id();

        mockMvc.perform(post("/web/categorias/" + id + "/excluir"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("sucesso", "Categoria excluída."));
    }

    @Test
    @DisplayName("Tela: exclui técnico sem chamados")
    void excluiTecnicoSemChamados() throws Exception {
        Long id = tecnicoService.criar(new TecnicoRequest("Temp", UUID.randomUUID() + "@teste.com", Perfil.TECNICO)).id();

        mockMvc.perform(post("/web/tecnicos/" + id + "/excluir"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("sucesso", "Técnico excluído."));
    }

    @Test
    @DisplayName("API: DELETE de cliente com chamados responde 409 (e não 500)")
    void apiNaoExcluiClienteComChamados() throws Exception {
        mockMvc.perform(delete("/clientes/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro").value("Não é possível excluir: este cliente possui chamados."));
    }

    @Test
    @DisplayName("API: DELETE de categoria com chamados responde 409")
    void apiNaoExcluiCategoriaComChamados() throws Exception {
        mockMvc.perform(delete("/categorias/1"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("API: DELETE de técnico com chamados responde 409")
    void apiNaoExcluiTecnicoComChamados() throws Exception {
        mockMvc.perform(delete("/tecnicos/2"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("API: GET /clientes devolve JSON com a chave 'telefone' em minúsculas")
    void apiListaClientes() throws Exception {
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").exists())
                .andExpect(jsonPath("$[0].telefone").exists());
    }

    @Test
    @DisplayName("API: cliente inexistente continua devolvendo 404 em JSON")
    void apiClienteInexistente() throws Exception {
        mockMvc.perform(get("/clientes/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    private String abrirChamado() throws Exception {
        String destino = mockMvc.perform(post("/web/chamados")
                        .param("titulo", "Chamado de teste " + UUID.randomUUID())
                        .param("descricao", "Criado pelo teste automático")
                        .param("prioridade", "BAIXA")
                        .param("clienteId", "1")
                        .param("categoriaId", "3"))
                .andExpect(status().is3xxRedirection())
                .andReturn().getResponse().getRedirectedUrl();
        return destino.substring(destino.lastIndexOf('/') + 1);
    }
}