package br.com.sistemas.chamados.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.sistemas.chamados.dto.TecnicoRequest;
import br.com.sistemas.chamados.dto.TecnicoResponse;
import br.com.sistemas.chamados.entity.Perfil;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.service.TecnicoService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/web/tecnicos")
public class TecnicoWebController {

    private final TecnicoService service;

    public TecnicoWebController(TecnicoService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        return montar(model, null, null, List.of());
    }

    @PostMapping
    public String criar(@Valid TecnicoRequest dto, BindingResult binding,
                        Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            return montar(model, null, dto, FormErros.de(binding));
        }
        try {
            service.criar(dto);
        } catch (RegraNegocioException ex) {
            return montar(model, null, dto, List.of(ex.getMessage()));
        }
        redirect.addFlashAttribute("sucesso", "Técnico cadastrado.");
        return "redirect:/web/tecnicos";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        TecnicoResponse tecnico = service.buscar(id);
        TecnicoRequest form = new TecnicoRequest(tecnico.nome(), tecnico.email(), tecnico.perfil());
        return montar(model, id, form, List.of());
    }

    @PostMapping("/{id}/editar")
    public String atualizar(@PathVariable Long id, @Valid TecnicoRequest dto, BindingResult binding,
                            Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            return montar(model, id, dto, FormErros.de(binding));
        }
        try {
            service.atualizar(id, dto);
        } catch (RegraNegocioException ex) {
            return montar(model, id, dto, List.of(ex.getMessage()));
        }
        redirect.addFlashAttribute("sucesso", "Técnico atualizado.");
        return "redirect:/web/tecnicos";
    }

    @GetMapping("/{id}/excluir")
    public String confirmarExclusao(@PathVariable Long id, Model model) {
        TecnicoResponse tecnico = service.buscar(id);
        model.addAttribute("ativo", "tecnicos");
        model.addAttribute("titulo", "Excluir técnico");
        model.addAttribute("pergunta", "Deseja excluir este técnico?");
        model.addAttribute("nomeItem", tecnico.nome());
        model.addAttribute("detalheItem", tecnico.email());
        model.addAttribute("rotuloDetalhe", "E-mail");
        model.addAttribute("rotuloBotao", "Excluir técnico");
        model.addAttribute("acao", "/web/tecnicos/" + id + "/excluir");
        model.addAttribute("voltar", "/web/tecnicos");
        return "confirmar-exclusao";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.excluir(id);
            redirect.addFlashAttribute("sucesso", "Técnico excluído.");
        } catch (RegraNegocioException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/web/tecnicos";
    }

    private String montar(Model model, Long idEdicao, TecnicoRequest form, List<String> erros) {
        model.addAttribute("tecnicos", service.listar());
        model.addAttribute("perfis", Perfil.values());
        model.addAttribute("form", form);
        model.addAttribute("erros", erros);
        model.addAttribute("editando", idEdicao != null);
        model.addAttribute("formTitulo", idEdicao != null ? "Editar técnico" : "Novo técnico");
        model.addAttribute("formAcao", idEdicao != null ? "/web/tecnicos/" + idEdicao + "/editar" : "/web/tecnicos");
        return "tecnicos/lista";
    }
}