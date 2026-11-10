package br.com.sistemas.chamados.web;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.sistemas.chamados.dto.AlterarStatusRequest;
import br.com.sistemas.chamados.dto.AtribuirTecnicoRequest;
import br.com.sistemas.chamados.dto.ChamadoRequest;
import br.com.sistemas.chamados.dto.ChamadoResponse;
import br.com.sistemas.chamados.dto.ComentarioRequest;
import br.com.sistemas.chamados.entity.Prioridade;
import br.com.sistemas.chamados.entity.StatusChamado;
import br.com.sistemas.chamados.exception.ChamadoFechadoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.service.CategoriaService;
import br.com.sistemas.chamados.service.ChamadoService;
import br.com.sistemas.chamados.service.ClienteService;
import br.com.sistemas.chamados.service.ComentarioService;
import br.com.sistemas.chamados.service.TecnicoService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/web/chamados")
public class ChamadoWebController {

    private static final String VOLTAR_AO_DETALHE = "redirect:/web/chamados/";

    private final ChamadoService chamadoService;
    private final ClienteService clienteService;
    private final CategoriaService categoriaService;
    private final TecnicoService tecnicoService;
    private final ComentarioService comentarioService;

    public ChamadoWebController(ChamadoService chamadoService,
                                ClienteService clienteService,
                                CategoriaService categoriaService,
                                TecnicoService tecnicoService,
                                ComentarioService comentarioService) {
        this.chamadoService = chamadoService;
        this.clienteService = clienteService;
        this.categoriaService = categoriaService;
        this.tecnicoService = tecnicoService;
        this.comentarioService = comentarioService;
    }

    @GetMapping
    public String listar(
            @RequestParam(required = false) StatusChamado status,
            @RequestParam(required = false) Prioridade prioridade,
            @RequestParam(required = false) Long clienteId,
            @PageableDefault(size = 5, sort = "dataAbertura", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        model.addAttribute("pagina", chamadoService.listarComFiltros(status, prioridade, clienteId, pageable));

        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("statusValores", StatusChamado.values());
        model.addAttribute("prioridadeValores", Prioridade.values());

        model.addAttribute("statusSel", status != null ? status.name() : "");
        model.addAttribute("prioridadeSel", prioridade != null ? prioridade.name() : "");
        model.addAttribute("clienteSel", clienteId);
        model.addAttribute("clienteParam", clienteId != null ? clienteId.toString() : "");

        return "chamados/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        return montarFormulario(model, null, List.of());
    }

    @PostMapping
    public String criar(@Valid ChamadoRequest dto, BindingResult binding,
                        Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            return montarFormulario(model, dto, FormErros.de(binding));
        }
        ChamadoResponse criado = chamadoService.criar(dto);
        redirect.addFlashAttribute("sucesso", "Chamado aberto.");
        return VOLTAR_AO_DETALHE + criado.id();
    }

    private String montarFormulario(Model model, ChamadoRequest form, List<String> erros) {
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("prioridadeValores", Prioridade.values());
        model.addAttribute("form", form);
        model.addAttribute("erros", erros);
        return "chamados/novo";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        ChamadoResponse chamado = chamadoService.buscar(id);

        model.addAttribute("chamado", chamado);
        model.addAttribute("fechado", "FECHADO".equals(chamado.status()));
        model.addAttribute("tecnicos", tecnicoService.listar());
        model.addAttribute("statusValores", StatusChamado.values());

        return "chamados/detalhe";
    }

    @PostMapping("/{id}/comentarios")
    public String comentar(@PathVariable Long id,
                           @Valid ComentarioRequest dto,
                           BindingResult binding,
                           RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            return erroDeValidacao(id, binding, redirect);
        }
        return executar(id, redirect, "Comentário adicionado.",
                () -> comentarioService.adicionar(id, dto));
    }

    @PostMapping("/{id}/atribuir")
    public String atribuir(@PathVariable Long id,
                           @Valid AtribuirTecnicoRequest dto,
                           BindingResult binding,
                           RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            return erroDeValidacao(id, binding, redirect);
        }
        return executar(id, redirect, "Técnico atribuído ao chamado.",
                () -> chamadoService.atribuirTecnico(id, dto));
    }

    @PostMapping("/{id}/status")
    public String alterarStatus(@PathVariable Long id,
                                @Valid AlterarStatusRequest dto,
                                BindingResult binding,
                                RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            return erroDeValidacao(id, binding, redirect);
        }
        return executar(id, redirect, "Status atualizado.",
                () -> chamadoService.alterarStatus(id, dto));
    }

    private String executar(Long id, RedirectAttributes redirect, String sucesso, Runnable acao) {
        try {
            acao.run();
            redirect.addFlashAttribute("sucesso", sucesso);
        } catch (RegraNegocioException | ChamadoFechadoException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
        }
        return VOLTAR_AO_DETALHE + id;
    }

    private String erroDeValidacao(Long id, BindingResult binding, RedirectAttributes redirect) {
        String mensagem = binding.getFieldErrors().isEmpty()
                ? "Dados inválidos"
                : binding.getFieldErrors().get(0).getDefaultMessage();
        redirect.addFlashAttribute("erro", mensagem);
        return VOLTAR_AO_DETALHE + id;
    }
}