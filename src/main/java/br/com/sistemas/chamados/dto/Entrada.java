package br.com.sistemas.chamados.dto;

/**
 * Regras compartilhadas de validação e normalização dos campos de entrada.
 * Os mesmos padrões são usados no front (atributos pattern dos inputs).
 */
public final class Entrada {

    /** Nome de pessoa: letras, espaço, ponto, apóstrofo e hífen. */
    public static final String NOME_PESSOA = "^\\p{L}[\\p{L} .'\\-]*$";

    /** Nome de empresa/categoria: letras, números e pontuação comum (sem < > etc). */
    public static final String NOME_GERAL = "^[\\p{L}\\p{N}][\\p{L}\\p{N} .,&'()/\\-]*$";

    /** Título: como o nome geral, com alguns sinais extras. */
    public static final String TITULO = "^[\\p{L}\\p{N}][\\p{L}\\p{N} .,&'()/:;!?#@%+\"\\-]*$";

    /** Texto livre: qualquer coisa, exceto marcação HTML. */
    public static final String TEXTO_LIVRE = "(?s)^[^<>]*$";

    /** E-mail: parte local, @, domínio com ponto e TLD de 2+ letras. */
    public static final String EMAIL = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9\\-]+(\\.[A-Za-z0-9\\-]+)*\\.[A-Za-z]{2,}$";

    /** Telefone brasileiro só com dígitos: DDD + 8 ou 9 dígitos. */
    public static final String TELEFONE = "^[1-9]{2}\\d{8,9}$";

    private Entrada() {
    }

    public static String texto(String valor) {
        return valor == null ? null : valor.strip();
    }

    public static String email(String valor) {
        return valor == null ? null : valor.strip().toLowerCase();
    }

    /** Remove a máscara do telefone; vazio vira null. Se tiver letras, mantém o valor para a validação rejeitar. */
    public static String telefone(String valor) {
        if (valor == null) {
            return null;
        }
        String limpo = valor.strip();
        if (limpo.isEmpty()) {
            return null;
        }
        if (limpo.matches("^[\\d()\\s.+\\-]+$")) {
            String digitos = limpo.replaceAll("\\D", "");
            if (digitos.startsWith("55") && digitos.length() > 11) {
                digitos = digitos.substring(2);
            }
            return digitos;
        }
        return limpo;
    }
}
