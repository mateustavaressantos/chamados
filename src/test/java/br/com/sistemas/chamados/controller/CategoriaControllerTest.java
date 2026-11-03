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

import br.com.sistemas.chamados.dto.CategoriaRequest;
import br.com.sistemas.chamados.dto.CategoriaResponse;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.service.CategoriaService;

@WebMvcTest(CategoriaController.class)
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaService service;

    @Test
    @DisplayName("Deve retornar 201 Created ao criar categoria com dados válidos")
    void deveRetornar201AoCriarCategoria() throws Exception {
        CategoriaResponse response = new CategoriaResponse(1L, "Hardware", "Problemas físicos");

        when(service.criar(any(CategoriaRequest.class))).thenReturn(response);

        mockMvc.perform(post("/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Hardware\",\"descricao\":\"Problemas físicos\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao criar categoria com nome em branco")
    void deveRetornar400QuandoDadosInvalidos() throws Exception {
        mockMvc.perform(post("/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"\",\"descricao\":\"Descrição válida\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 409 Conflict quando nome da categoria já existe")
    void deveRetornar409QuandoNomeDuplicado() throws Exception {
        when(service.criar(any(CategoriaRequest.class)))
                .thenThrow(new RegraNegocioException("Categoria com este nome já existe"));

        mockMvc.perform(post("/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Hardware\",\"descricao\":\"Problemas físicos\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao buscar ID inexistente")
    void deveRetornar404QuandoBuscaIdInexistente() throws Exception {
        when(service.buscar(99L))
                .thenThrow(new RecursoNaoEncontradoException("Categoria não encontrada"));

        mockMvc.perform(get("/categorias/99"))
                .andExpect(status().isNotFound());
    }
}