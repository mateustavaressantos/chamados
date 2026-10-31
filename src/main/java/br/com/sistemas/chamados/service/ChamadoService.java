package br.com.sistemas.chamados.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.sistemas.chamados.dto.AlterarStatusRequest;
import br.com.sistemas.chamados.dto.AtribuirTecnicoRequest;
import br.com.sistemas.chamados.dto.ChamadoRequest;
import br.com.sistemas.chamados.dto.ChamadoResponse;
import br.com.sistemas.chamados.entity.Categoria;
import br.com.sistemas.chamados.entity.Chamado;
import br.com.sistemas.chamados.entity.Cliente;
import br.com.sistemas.chamados.entity.Comentario;
import br.com.sistemas.chamados.entity.StatusChamado;
import br.com.sistemas.chamados.entity.Tecnico;
import br.com.sistemas.chamados.exception.ChamadoFechadoException;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.repository.CategoriaRepository;
import br.com.sistemas.chamados.repository.ChamadoRepository;
import br.com.sistemas.chamados.repository.ClienteRepository;
import br.com.sistemas.chamados.repository.ComentarioRepository;
import br.com.sistemas.chamados.repository.TecnicoRepository;
import br.com.sistemas.chamados.entity.Prioridade;

@Service
public class ChamadoService {

    private final ChamadoRepository repository;
    private final ClienteRepository clienteRepository;
    private final CategoriaRepository categoriaRepository;
    private final TecnicoRepository tecnicoRepository;
    private final ComentarioRepository comentarioRepository;

    public ChamadoService(ChamadoRepository repository, ClienteRepository clienteRepository, CategoriaRepository categoriaRepository, TecnicoRepository tecnicoRepository, ComentarioRepository comentarioRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
        this.categoriaRepository = categoriaRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.comentarioRepository = comentarioRepository;
    }

    @Transactional
    public ChamadoResponse criar(ChamadoRequest dto) {
        // Busca o Cliente na base de dados pelo ID
        Cliente cliente = clienteRepository.findById(dto.clienteId()).orElseThrow(() -> new RecursoNaoEncontradoException("Cliente " + dto.clienteId() + " não encontrado"));

        // Buscar a Categoria na base de dados pelo ID
        Categoria categoria = categoriaRepository.findById(dto.categoriaId()).orElseThrow(() -> new RecursoNaoEncontradoException("Categoria " + dto.categoriaId() + " não encontrada"));

        // Cria o chamado (o construtor já define o status ABERTO e a data)
        Chamado chamado = new Chamado(dto.titulo(), dto.descricao(), dto.prioridade(), cliente, categoria);

        // Guarda na base de dados
        Chamado salvo = repository.save(chamado);
        
        return ChamadoResponse.de(salvo);
    }

    @Transactional(readOnly = true)
    public ChamadoResponse buscar(Long id) {
        return ChamadoResponse.de(buscarEntity(id));
    }

    private Chamado buscarEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Chamado " + id + " não encontrado"));
    }

    @Transactional
    public ChamadoResponse atribuirTecnico(Long id, AtribuirTecnicoRequest dto) {
        Chamado chamado = buscarEntity(id);

        // Impedir atribuição se o chamado já estiver fechado
        if (chamado.getStatus() == StatusChamado.FECHADO) {
            throw new ChamadoFechadoException("Não é possível atribuir um técnico a um chamado fechado.");
        }

        // Busca o técnico no banco
        Tecnico tecnico = tecnicoRepository.findById(dto.tecnicoId()).orElseThrow(() -> new RecursoNaoEncontradoException("Técnico " + dto.tecnicoId() + " não encontrado"));

        chamado.setTecnico(tecnico);
        
        if(chamado.getStatus() == StatusChamado.ABERTO) {
            chamado.setStatus(StatusChamado.EM_ATENDIMENTO);
        }

        Comentario historicoAtribuicao = new Comentario(
            "Técnico " + tecnico.getNome() + " foi atribuído ao chamado.",
            "Sistema",
            chamado
        );
        comentarioRepository.save(historicoAtribuicao);

        return ChamadoResponse.de(chamado);
    }

    @Transactional
    public ChamadoResponse alterarStatus(Long id, AlterarStatusRequest dto) {
        Chamado chamado = buscarEntity(id);

        // Impedir alteração se o chamado já estiver fechado
        if (chamado.getStatus() == StatusChamado.FECHADO) {
            throw new ChamadoFechadoException("Este chamado já foi fechado e não pode ser alterado.");
        }

        chamado.setStatus(dto.status());

        // Ao fechar, preencher a data de fechamento automaticamente
        if (dto.status() == StatusChamado.FECHADO) {
            chamado.setDataFechamento(java.time.LocalDateTime.now());
        }

        Comentario historicoStatus = new Comentario(
            "O status do chamado foi alterado para: " + dto.status(),
            "Sistema",
            chamado
        );
        comentarioRepository.save(historicoStatus);

        return ChamadoResponse.de(chamado);
    }

    @Transactional(readOnly = true)
    public Page<ChamadoResponse> listarComFiltros(StatusChamado status, Prioridade prioridade, Long clienteId, Pageable pageable) {
        return repository.filtrar(status, prioridade, clienteId, pageable).map(ChamadoResponse::de);
    }

    @Transactional(readOnly = true)
    public Page<ChamadoResponse> listarPorCliente(Long clienteId, Pageable pageable) {
        // Valida se o cliente existe antes de buscar os chamados
        if (!clienteRepository.existsById(clienteId)) {
            throw new RecursoNaoEncontradoException("Cliente " + clienteId + " não encontrado");
        }
        
        return repository.findByClienteId(clienteId, pageable).map(ChamadoResponse::de);
    }
}