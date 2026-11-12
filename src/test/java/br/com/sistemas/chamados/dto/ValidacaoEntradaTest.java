package br.com.sistemas.chamados.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import br.com.sistemas.chamados.entity.Perfil;
import br.com.sistemas.chamados.entity.Prioridade;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class ValidacaoEntradaTest {

    private static Validator validator;

    @BeforeAll
    static void criarValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private static <T> boolean valido(T dto) {
        Set<ConstraintViolation<T>> erros = validator.validate(dto);
        return erros.isEmpty();
    }

    // ---------- Telefone / máscara ----------

    @ParameterizedTest
    @ValueSource(strings = {"47999999999", "(47) 99999-9999", "4733330000", "(47) 3333-0000", "47 99999 9999"})
    @DisplayName("Telefone válido, com ou sem máscara, é aceito")
    void telefoneValido(String telefone) {
        assertThat(valido(new ClienteRequest("Cliente Teste", "a@teste.com", telefone))).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "123", "(47) 9999", "479999999999999", "00999999999", "47-ABCDE-1234"})
    @DisplayName("Telefone inválido é rejeitado")
    void telefoneInvalido(String telefone) {
        assertThat(valido(new ClienteRequest("Cliente Teste", "a@teste.com", telefone))).isFalse();
    }

    @Test
    @DisplayName("Máscara do telefone é removida e só os dígitos são guardados")
    void telefoneNormalizado() {
        assertThat(new ClienteRequest("Cliente", "a@teste.com", "(47) 99999-9999").telefone())
                .isEqualTo("47999999999");
        assertThat(new ClienteRequest("Cliente", "a@teste.com", "+55 (47) 99999-9999").telefone())
                .isEqualTo("47999999999");
    }

    @Test
    @DisplayName("Telefone em branco é opcional (vira null)")
    void telefoneOpcional() {
        ClienteRequest dto = new ClienteRequest("Cliente", "a@teste.com", "   ");
        assertThat(dto.telefone()).isNull();
        assertThat(valido(dto)).isTrue();
    }

    // ---------- E-mail ----------

    @ParameterizedTest
    @ValueSource(strings = {"a@b", "sem-arroba.com", "a@b.c", "@teste.com", "a b@teste.com", "a@@teste.com"})
    @DisplayName("E-mail inválido é rejeitado (inclusive 'a@b', que o @Email padrão aceitava)")
    void emailInvalido(String email) {
        assertThat(valido(new ClienteRequest("Cliente Teste", email, null))).isFalse();
    }

    @Test
    @DisplayName("E-mail é aparado e convertido para minúsculas")
    void emailNormalizado() {
        assertThat(new ClienteRequest("Cliente", "  Fulano@Teste.COM ", null).email())
                .isEqualTo("fulano@teste.com");
    }

    @Test
    @DisplayName("E-mail acima de 120 caracteres é rejeitado")
    void emailMuitoLongo() {
        String longo = "a".repeat(115) + "@t.com";
        assertThat(valido(new ClienteRequest("Cliente Teste", longo, null))).isFalse();
    }

    // ---------- Nomes ----------

    @ParameterizedTest
    @ValueSource(strings = {"A", "<script>alert(1)</script>", "Nome; DROP TABLE", "123", "Nome\nQuebra"})
    @DisplayName("Nome de técnico com caracteres inválidos ou curto demais é rejeitado")
    void nomeTecnicoInvalido(String nome) {
        assertThat(valido(new TecnicoRequest(nome, "t@teste.com", Perfil.TECNICO))).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Ana Beatriz", "João D'Ávila", "Maria-Clara", "José A. Silva"})
    @DisplayName("Nome de técnico com acentos, apóstrofo, hífen e ponto é aceito")
    void nomeTecnicoValido(String nome) {
        assertThat(valido(new TecnicoRequest(nome, "t@teste.com", Perfil.TECNICO))).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Escritório Silva & Associados", "Tech Solutions Ltda", "Empresa Para-Chamados", "Loja 24h (Centro)"})
    @DisplayName("Nome de cliente de empresa aceita números e pontuação comum")
    void nomeClienteValido(String nome) {
        assertThat(valido(new ClienteRequest(nome, "c@teste.com", null))).isTrue();
    }

    @Test
    @DisplayName("Nome de cliente com marcação HTML é rejeitado")
    void nomeClienteComHtml() {
        assertThat(valido(new ClienteRequest("<b>Cliente</b>", "c@teste.com", null))).isFalse();
    }

    @Test
    @DisplayName("Espaços nas pontas são removidos; nome só com espaços é rejeitado")
    void trim() {
        assertThat(new ClienteRequest("  Cliente  ", "c@teste.com", null).nome()).isEqualTo("Cliente");
        assertThat(valido(new ClienteRequest("     ", "c@teste.com", null))).isFalse();
    }

    // ---------- Texto livre ----------

    @Test
    @DisplayName("Descrição de chamado com < ou > é rejeitada")
    void descricaoComHtml() {
        assertThat(valido(new ChamadoRequest("Título ok", "<img src=x onerror=alert(1)>", Prioridade.BAIXA, 1L, 1L)))
                .isFalse();
    }

    @Test
    @DisplayName("Chamado válido passa; título curto, descrição curta e IDs não positivos falham")
    void chamado() {
        assertThat(valido(new ChamadoRequest("Internet lenta", "A conexão cai toda hora.", Prioridade.MEDIA, 1L, 1L))).isTrue();
        assertThat(valido(new ChamadoRequest("ab", "A conexão cai toda hora.", Prioridade.MEDIA, 1L, 1L))).isFalse();
        assertThat(valido(new ChamadoRequest("Internet lenta", "abcd", Prioridade.MEDIA, 1L, 1L))).isFalse();
        assertThat(valido(new ChamadoRequest("Internet lenta", "A conexão cai toda hora.", Prioridade.MEDIA, 0L, 1L))).isFalse();
        assertThat(valido(new ChamadoRequest("Internet lenta", "A conexão cai toda hora.", Prioridade.MEDIA, 1L, -5L))).isFalse();
    }

    @Test
    @DisplayName("Título aceita pontuação comum e rejeita < >")
    void titulo() {
        assertThat(valido(new ChamadoRequest("Erro 500: relatório (mensal)!", "Descrição válida", Prioridade.ALTA, 1L, 1L))).isTrue();
        assertThat(valido(new ChamadoRequest("<script>", "Descrição válida", Prioridade.ALTA, 1L, 1L))).isFalse();
    }

    // ---------- Comentário e categoria ----------

    @Test
    @DisplayName("Comentário: autor com símbolos e texto com HTML são rejeitados")
    void comentario() {
        assertThat(valido(new ComentarioRequest("Tudo certo", "Valeska Ingrid"))).isTrue();
        assertThat(valido(new ComentarioRequest("Tudo certo", "Autor<1>"))).isFalse();
        assertThat(valido(new ComentarioRequest("<script>x</script>", "Valeska"))).isFalse();
        assertThat(valido(new ComentarioRequest("a".repeat(1001), "Valeska"))).isFalse();
    }

    @Test
    @DisplayName("Categoria: nome e descrição respeitam tamanhos mínimos e caracteres permitidos")
    void categoria() {
        assertThat(valido(new CategoriaRequest("Acesso e Senhas", "Redefinição de senha e permissões"))).isTrue();
        assertThat(valido(new CategoriaRequest("E-mail", "Contas de e-mail"))).isTrue();
        assertThat(valido(new CategoriaRequest("X", "Descrição válida"))).isFalse();
        assertThat(valido(new CategoriaRequest("Categoria <b>", "Descrição válida"))).isFalse();
        assertThat(valido(new CategoriaRequest("Categoria", "abc"))).isFalse();
    }
}
