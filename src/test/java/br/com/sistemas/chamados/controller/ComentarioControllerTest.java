package br.com.sistemas.chamados.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.sistemas.chamados.dto.ComentarioRequest;
import br.com.sistemas.chamados.exception.ChamadoFechadoException;
import br.com.sistemas.chamados.service.ComentarioService;

@WebMvcTest(ComentarioController.class)
class ComentarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ComentarioService service;

    @Test
    @DisplayName("Deve retornar 201 Created ao adicionar comentario em chamado aberto")
    void deveRetornar201AoCriarComentario() throws Exception {
        br.com.sistemas.chamados.dto.ComentarioResponse response = new br.com.sistemas.chamados.dto.ComentarioResponse(1L, "Verifiquem a fonte", "Sistema", java.time.LocalDateTime.now());

        when(service.adicionar(eq(1L), any(ComentarioRequest.class))).thenReturn(response);

        mockMvc.perform(post("/chamados/1/comentarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"texto\":\"Verifiquem a fonte\",\"autor\":\"Sistema\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao enviar comentario sem texto")
    void deveRetornar400QuandoDadosInvalidos() throws Exception {
        mockMvc.perform(post("/chamados/1/comentarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"texto\":\"\",\"autor\":\"Sistema\"}"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Deve retornar 409 Conflict ao tentar comentar em chamado fechado")
    void deveRetornar409AoComentarEmChamadoFechado() throws Exception {
        when(service.adicionar(eq(1L), any(ComentarioRequest.class)))
                .thenThrow(new ChamadoFechadoException("Chamado ja encerrado"));

        mockMvc.perform(post("/chamados/1/comentarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"texto\":\"Este chamado ja era\",\"autor\":\"Sistema\"}"))
                .andExpect(status().isConflict());
    }
}