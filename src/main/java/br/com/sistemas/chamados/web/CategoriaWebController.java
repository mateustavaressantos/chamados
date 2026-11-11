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

import br.com.sistemas.chamados.dto.CategoriaRequest;
import br.com.sistemas.chamados.dto.CategoriaResponse;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.service.CategoriaService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/web/categorias")
public class CategoriaWebController {

    private final CategoriaService service;

    public CategoriaWebController(CategoriaService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        return montar(model, null, null, List.of());
    }

    @PostMapping
    public String criar(@Valid CategoriaRequest dto, BindingResult binding,
                        Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            return montar(model, null, dto, FormErros.de(binding));
        }
        try {
            service.criar(dto);
        } catch (RegraNegocioException ex) {
            return montar(model, null, dto, List.of(ex.getMessage()));
        }
        redirect.addFlashAttribute("sucesso", "Categoria cadastrada.");
        return "redirect:/web/categorias";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        CategoriaResponse categoria = service.buscar(id);
        CategoriaRequest form = new CategoriaRequest(categoria.nome(), categoria.descricao());
        return montar(model, id, form, List.of());
    }

    @PostMapping("/{id}/editar")
    public String atualizar(@PathVariable Long id, @Valid CategoriaRequest dto, BindingResult binding,
                            Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            return montar(model, id, dto, FormErros.de(binding));
        }
        try {
            service.atualizar(id, dto);
        } catch (RegraNegocioException ex) {
            return montar(model, id, dto, List.of(ex.getMessage()));
        }
        redirect.addFlashAttribute("sucesso", "Categoria atualizada.");
        return "redirect:/web/categorias";
    }

    @GetMapping("/{id}/excluir")
    public String confirmarExclusao(@PathVariable Long id, Model model) {
        CategoriaResponse categoria = service.buscar(id);
        model.addAttribute("ativo", "categorias");
        model.addAttribute("titulo", "Excluir categoria");
        model.addAttribute("pergunta", "Deseja excluir esta categoria?");
        model.addAttribute("nomeItem", categoria.nome());
        model.addAttribute("detalheItem", categoria.descricao());
        model.addAttribute("rotuloDetalhe", "Descrição");
        model.addAttribute("rotuloBotao", "Excluir categoria");
        model.addAttribute("acao", "/web/categorias/" + id + "/excluir");
        model.addAttribute("voltar", "/web/categorias");
        return "confirmar-exclusao";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.excluir(id);
            redirect.addFlashAttribute("sucesso", "Categoria excluída.");
        } catch (RegraNegocioException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/web/categorias";
    }

    private String montar(Model model, Long idEdicao, CategoriaRequest form, List<String> erros) {
        model.addAttribute("categorias", service.listar());
        model.addAttribute("form", form);
        model.addAttribute("erros", erros);
        model.addAttribute("editando", idEdicao != null);
        model.addAttribute("formTitulo", idEdicao != null ? "Editar categoria" : "Nova categoria");
        model.addAttribute("formAcao", idEdicao != null ? "/web/categorias/" + idEdicao + "/editar" : "/web/categorias");
        return "categorias/lista";
    }
}