/*
 * Máscaras e validação no navegador.
 * A validação real continua no servidor (Bean Validation); aqui só damos feedback rápido.
 *
 * Uso nos inputs:
 *   data-mask="telefone"   -> aplica máscara (00) 00000-0000
 *   data-format="telefone" -> formata texto exibido (ex.: células da tabela)
 */
(function () {
    'use strict';

    function soDigitos(v) {
        return (v || '').replace(/\D/g, '');
    }

    function formatarTelefone(v) {
        var d = soDigitos(v).slice(0, 11);
        if (d.length === 0) return '';
        if (d.length <= 2) return '(' + d;
        if (d.length <= 6) return '(' + d.slice(0, 2) + ') ' + d.slice(2);
        if (d.length <= 10) return '(' + d.slice(0, 2) + ') ' + d.slice(2, 6) + '-' + d.slice(6);
        return '(' + d.slice(0, 2) + ') ' + d.slice(2, 7) + '-' + d.slice(7);
    }

    var mascaras = { telefone: formatarTelefone };

    var mensagens = {
        valueMissing: 'Preencha este campo.',
        typeMismatch: 'Valor inválido.',
        tooShort: function (el) { return 'Digite pelo menos ' + el.minLength + ' caracteres.'; },
        tooLong: function (el) { return 'Máximo de ' + el.maxLength + ' caracteres.'; }
    };

    function mensagemDe(el) {
        var v = el.validity;
        if (v.valueMissing) return mensagens.valueMissing;
        if (v.tooShort) return mensagens.tooShort(el);
        if (v.tooLong) return mensagens.tooLong(el);
        if (v.typeMismatch || v.patternMismatch) {
            return el.getAttribute('data-erro') || mensagens.typeMismatch;
        }
        return el.validationMessage;
    }

    function validarExtra(el) {
        el.setCustomValidity('');
        if (el.value && el.hasAttribute('data-mask')) {
            var d = soDigitos(el.value);
            if (el.getAttribute('data-mask') === 'telefone' && (d.length < 10 || d.length > 11)) {
                el.setCustomValidity('Telefone incompleto. Use DDD + número.');
            }
        }
        // Texto só com espaços não conta como preenchido
        if (el.required && el.value && el.value.trim() === '' && !el.disabled) {
            el.setCustomValidity('Preencha este campo.');
        }
    }

    function mostrarErro(el) {
        var msg = el.validationMessage && el.validity.customError ? el.validationMessage : mensagemDe(el);
        el.setCustomValidity(msg);
        el.classList.add('invalido');
    }

    function revalidar(el) {
        validarExtra(el);
        if (el.checkValidity()) {
            el.classList.remove('invalido');
        }
    }

    document.addEventListener('DOMContentLoaded', function () {
        // Máscaras
        document.querySelectorAll('[data-mask]').forEach(function (el) {
            var fn = mascaras[el.getAttribute('data-mask')];
            if (!fn) return;
            el.value = fn(el.value);
            el.addEventListener('input', function () {
                el.value = fn(el.value);
            });
        });

        // Contador de caracteres (campos com maxlength)
        document.querySelectorAll('input[maxlength], textarea[maxlength]').forEach(function (el) {
            if (el.type === 'hidden' || el.hasAttribute('data-mask')) return;
            var max = el.maxLength;
            var min = el.minLength > 0 ? el.minLength : 0;
            var span = document.createElement('span');
            span.className = 'contador';
            span.setAttribute('aria-live', 'polite');
            el.insertAdjacentElement('afterend', span);
            function atualizar() {
                var n = el.value.length;
                var texto = n + '/' + max + ' caracteres';
                if (min && n < min) texto += ' (mínimo ' + min + ')';
                span.textContent = texto;
                span.classList.toggle('limite', n >= max);
                span.classList.toggle('perto', n < max && n >= max * 0.9);
            }
            el.addEventListener('input', atualizar);
            atualizar();
        });

        // Texto formatado (somente exibição)
        document.querySelectorAll('[data-format]').forEach(function (el) {
            var fn = mascaras[el.getAttribute('data-format')];
            if (fn && el.textContent.trim()) el.textContent = fn(el.textContent);
        });

        // Validação dos formulários
        document.querySelectorAll('form').forEach(function (form) {
            var campos = form.querySelectorAll('input:not([type=hidden]), textarea, select');

            campos.forEach(function (el) {
                el.addEventListener('input', function () { revalidar(el); });
                el.addEventListener('change', function () { revalidar(el); });
                el.addEventListener('invalid', function () { mostrarErro(el); });
            });

            form.addEventListener('submit', function (e) {
                var primeiro = null;
                campos.forEach(function (el) {
                    if (el.disabled) return;
                    // Remove espaços nas pontas antes de validar
                    if (el.tagName !== 'SELECT' && el.type !== 'password') {
                        el.value = el.value.trim();
                    }
                    el.setCustomValidity('');
                    validarExtra(el);
                    if (!el.checkValidity()) {
                        mostrarErro(el);
                        if (!primeiro) primeiro = el;
                    }
                });
                if (primeiro) {
                    e.preventDefault();
                    primeiro.reportValidity();
                    primeiro.focus();
                }
            });
        });
    });
})();
