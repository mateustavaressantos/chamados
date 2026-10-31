package br.com.sistemas.chamados.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.sistemas.chamados.dto.AlterarStatusRequest;
import br.com.sistemas.chamados.dto.ChamadoRequest;
import br.com.sistemas.chamados.exception.ChamadoFechadoException;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.service.ChamadoService;

@WebMvcTest(ChamadoController.class)
class ChamadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChamadoService service;

    @Test
    @DisplayName("Deve retornar 201 Created ao abrir chamado com dados validos")
    void deveRetornar201AoAbrirChamado() throws Exception {
        
        // Agora instanciamos o ChamadoResponse com a ordem e os tipos exatos do seu record!
        br.com.sistemas.chamados.dto.ChamadoResponse response = new br.com.sistemas.chamados.dto.ChamadoResponse(
            1L, "Erro", "Erro", "ABERTO", "ALTA", java.time.LocalDateTime.now(), null, "Mateus", null, java.util.List.of());

        when(service.criar(any(ChamadoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/chamados")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"titulo\":\"Erro\",\"descricao\":\"Erro\",\"prioridade\":\"ALTA\",\"clienteId\":1,\"categoriaId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao abrir chamado com dados em branco")
    void deveRetornar400QuandoDadosInvalidos() throws Exception {
        mockMvc.perform(post("/chamados")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"titulo\":\"\",\"descricao\":\"\",\"prioridade\":\"ALTA\",\"clienteId\":1,\"categoriaId\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 409 Conflict ao tentar alterar status de chamado fechado")
    void deveRetornar409AoAlterarChamadoFechado() throws Exception {
        when(service.alterarStatus(eq(1L), any(AlterarStatusRequest.class)))
                .thenThrow(new ChamadoFechadoException("Este chamado ja foi encerrado"));

        mockMvc.perform(patch("/chamados/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"EM_ATENDIMENTO\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao buscar chamado com ID inexistente")
    void deveRetornar404QuandoBuscaIdInexistente() throws Exception {
        when(service.buscar(99L))
                .thenThrow(new RecursoNaoEncontradoException("Chamado nao encontrado"));

        mockMvc.perform(get("/chamados/99"))
                .andExpect(status().isNotFound());
    }
}