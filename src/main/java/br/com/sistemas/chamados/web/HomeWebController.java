package br.com.sistemas.chamados.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import br.com.sistemas.chamados.entity.Prioridade;
import br.com.sistemas.chamados.entity.StatusChamado;
import br.com.sistemas.chamados.service.ChamadoService;

@Controller
public class HomeWebController {

    public record Indicador(String rotulo, String nome, long quantidade, int largura) {
    }

    private final ChamadoService chamadoService;

    public HomeWebController(ChamadoService chamadoService) {
        this.chamadoService = chamadoService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        Pageable apenasTotal = PageRequest.of(0, 1);

        List<Indicador> porStatus = new ArrayList<>();
        for (StatusChamado status : StatusChamado.values()) {
            long total = chamadoService.listarComFiltros(status, null, null, apenasTotal).getTotalElements();
            porStatus.add(new Indicador(rotulo(status.name()), status.name(), total, 0));
        }

        List<Indicador> porPrioridade = new ArrayList<>();
        for (Prioridade prioridade : Prioridade.values()) {
            long total = chamadoService.listarComFiltros(null, prioridade, null, apenasTotal).getTotalElements();
            porPrioridade.add(new Indicador(rotulo(prioridade.name()), prioridade.name(), total, 0));
        }

        Pageable ultimos = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "dataAbertura"));

        model.addAttribute("porStatus", comLargura(porStatus));
        model.addAttribute("porPrioridade", comLargura(porPrioridade));
        model.addAttribute("ultimos", chamadoService.listarComFiltros(null, null, null, ultimos).getContent());
        return "dashboard";
    }

    private List<Indicador> comLargura(List<Indicador> indicadores) {
        long maior = indicadores.stream().mapToLong(Indicador::quantidade).max().orElse(0);
        return indicadores.stream()
                .map(i -> new Indicador(i.rotulo(), i.nome(), i.quantidade(),
                        maior == 0 ? 0 : (int) (i.quantidade() * 100 / maior)))
                .toList();
    }

    private String rotulo(String nome) {
        return switch (nome) {
            case "ABERTO" -> "Aberto";
            case "EM_ATENDIMENTO" -> "Em atendimento";
            case "AGUARDANDO_CLIENTE" -> "Aguardando cliente";
            case "FECHADO" -> "Fechado";
            case "BAIXA" -> "Baixa";
            case "MEDIA" -> "Média";
            case "ALTA" -> "Alta";
            case "CRITICA" -> "Crítica";
            default -> nome;
        };
    }
}