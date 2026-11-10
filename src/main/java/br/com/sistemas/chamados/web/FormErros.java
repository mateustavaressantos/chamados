package br.com.sistemas.chamados.web;

import java.util.List;

import org.springframework.validation.BindingResult;

final class FormErros {

    private FormErros() {
    }

    static List<String> de(BindingResult binding) {
        List<String> mensagens = binding.getFieldErrors().stream()
                .map(erro -> erro.getDefaultMessage())
                .toList();
        return mensagens.isEmpty() ? List.of("Dados inválidos") : mensagens;
    }
}