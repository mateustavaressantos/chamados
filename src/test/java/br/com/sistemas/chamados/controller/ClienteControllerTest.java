package br.com.sistemas.chamados.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.sistemas.chamados.dto.ClienteRequest;
import br.com.sistemas.chamados.dto.ClienteResponse;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.service.ClienteService;
import br.com.sistemas.chamados.service.ChamadoService;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService service;

    @MockitoBean
    private ChamadoService chamadoService;

    @Test
    @DisplayName("Deve retornar 201 Created ao criar cliente com dados válidos")
    void deveRetornar201AoCriarCliente() throws Exception {
        ClienteResponse response = new ClienteResponse(1L, "Mateus", "mateus@gmail.com", "47999999999");

        when(service.criar(any(ClienteRequest.class))).thenReturn(response);

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Mateus\",\"email\":\"mateus@gmail.com\",\"telefone\":\"47999999999\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao criar cliente com e-mail inválido")
    void deveRetornar400QuandoDadosInvalidos() throws Exception {
        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Mateus\",\"email\":\"email_invalido\",\"telefone\":\"47999999999\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 409 Conflict quando e-mail já existe")
    void deveRetornar409QuandoEmailDuplicado() throws Exception {
        when(service.criar(any(ClienteRequest.class)))
                .thenThrow(new RegraNegocioException("Já existe cliente com o e-mail mateus@gmail.com"));

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Mateus\",\"email\":\"mateus@gmail.com\",\"telefone\":\"47999999999\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao buscar ID inexistente")
    void deveRetornar404QuandoBuscaIdInexistente() throws Exception {
        when(service.buscar(99L))
                .thenThrow(new RecursoNaoEncontradoException("Cliente 99 não encontrado"));

        mockMvc.perform(get("/clientes/99"))
                .andExpect(status().isNotFound());
    }
}