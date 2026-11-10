package br.com.sistemas.chamados.web;

import java.util.List;

import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.sistemas.chamados.dto.ClienteRequest;
import br.com.sistemas.chamados.dto.ClienteResponse;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.service.ChamadoService;
import br.com.sistemas.chamados.service.ClienteService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/web/clientes")
public class ClienteWebController {

    public record ClienteLinha(ClienteResponse cliente, long totalChamados) {
    }

    private final ClienteService clienteService;
    private final ChamadoService chamadoService;

    public ClienteWebController(ClienteService clienteService, ChamadoService chamadoService) {
        this.clienteService = clienteService;
        this.chamadoService = chamadoService;
    }

    @GetMapping
    public String listar(Model model) {
        return montarLista(model, null, List.of());
    }

    @PostMapping
    public String criar(@Valid ClienteRequest dto, BindingResult binding,
                        Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            return montarLista(model, dto, FormErros.de(binding));
        }
        try {
            clienteService.criar(dto);
        } catch (RegraNegocioException ex) {
            return montarLista(model, dto, List.of(ex.getMessage()));
        }
        redirect.addFlashAttribute("sucesso", "Cliente cadastrado.");
        return "redirect:/web/clientes";
    }

    private String montarLista(Model model, ClienteRequest form, List<String> erros) {
        List<ClienteLinha> linhas = clienteService.listar().stream()
                .map(c -> new ClienteLinha(c, totalDeChamados(c.id())))
                .toList();
        model.addAttribute("linhas", linhas);
        model.addAttribute("form", form);
        model.addAttribute("erros", erros);
        return "clientes/lista";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        ClienteResponse cliente = clienteService.buscar(id);
        ClienteRequest form = new ClienteRequest(cliente.nome(), cliente.email(), cliente.telefone());
        return montarEdicao(model, cliente, form, List.of());
    }

    @PostMapping("/{id}/editar")
    public String atualizar(@PathVariable Long id, @Valid ClienteRequest dto, BindingResult binding,
                            Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            return montarEdicao(model, clienteService.buscar(id), dto, FormErros.de(binding));
        }
        try {
            clienteService.atualizar(id, dto);
        } catch (RegraNegocioException ex) {
            return montarEdicao(model, clienteService.buscar(id), dto, List.of(ex.getMessage()));
        }
        redirect.addFlashAttribute("sucesso", "Cliente atualizado.");
        return "redirect:/web/clientes";
    }

    private String montarEdicao(Model model, ClienteResponse cliente, ClienteRequest form, List<String> erros) {
        model.addAttribute("cliente", cliente);
        model.addAttribute("totalChamados", totalDeChamados(cliente.id()));
        model.addAttribute("form", form);
        model.addAttribute("erros", erros);
        return "clientes/editar";
    }

    @GetMapping("/{id}/excluir")
    public String confirmarExclusao(@PathVariable Long id, Model model) {
        ClienteResponse cliente = clienteService.buscar(id);
        model.addAttribute("ativo", "clientes");
        model.addAttribute("titulo", "Excluir cliente");
        model.addAttribute("pergunta", "Deseja excluir este cliente?");
        model.addAttribute("nomeItem", cliente.nome());
        model.addAttribute("detalheItem", cliente.email());
        model.addAttribute("rotuloDetalhe", "E-mail");
        model.addAttribute("rotuloBotao", "Excluir cliente");
        model.addAttribute("acao", "/web/clientes/" + id + "/excluir");
        model.addAttribute("voltar", "/web/clientes");
        return "confirmar-exclusao";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            clienteService.excluir(id);
            redirect.addFlashAttribute("sucesso", "Cliente excluído.");
        } catch (RegraNegocioException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/web/clientes";
    }

    @GetMapping("/{id}/chamados")
    public String chamadosDoCliente(
            @PathVariable Long id,
            @PageableDefault(size = 10, sort = "dataAbertura", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        model.addAttribute("cliente", clienteService.buscar(id));
        model.addAttribute("pagina", chamadoService.listarPorCliente(id, pageable));
        return "clientes/chamados";
    }

    private long totalDeChamados(Long clienteId) {
        return chamadoService.listarPorCliente(clienteId, PageRequest.of(0, 1)).getTotalElements();
    }
}