package br.com.sistemas.chamados.exception;

/* Lançada quando há tentativa de alterar um chamado já finalizado (vira HTTP 409). */
public class ChamadoFechadoException extends RuntimeException {
    
    public ChamadoFechadoException(String mensagem) {
        super(mensagem);
    }
}