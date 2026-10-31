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

import br.com.sistemas.chamados.dto.TecnicoRequest;
import br.com.sistemas.chamados.dto.TecnicoResponse;
import br.com.sistemas.chamados.entity.Perfil;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.service.TecnicoService;

@WebMvcTest(TecnicoController.class)
class TecnicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TecnicoService service;

    @Test
    @DisplayName("Deve retornar 201 Created ao criar tecnico com dados validos")
    void deveRetornar201AoCriarTecnico() throws Exception {
        TecnicoResponse response = new TecnicoResponse(1L, "Emilly", "emilly@gmail.com", Perfil.TECNICO);

        when(service.criar(any(TecnicoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/tecnicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Emilly\",\"email\":\"emilly@gmail.com\",\"perfil\":\"TECNICO\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao criar tecnico com email invalido")
    void deveRetornar400QuandoDadosInvalidos() throws Exception {
        mockMvc.perform(post("/tecnicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Emilly\",\"email\":\"email_invalido\",\"perfil\":\"TECNICO\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 409 Conflict quando email do tecnico ja existe")
    void deveRetornar409QuandoEmailDuplicado() throws Exception {
        when(service.criar(any(TecnicoRequest.class)))
                .thenThrow(new RegraNegocioException("Ja existe tecnico com o e-mail emilly@gmail.com"));

        mockMvc.perform(post("/tecnicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Emilly\",\"email\":\"emilly@gmail.com\",\"perfil\":\"TECNICO\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao buscar ID inexistente")
    void deveRetornar404QuandoBuscaIdInexistente() throws Exception {
        when(service.buscar(99L))
                .thenThrow(new RecursoNaoEncontradoException("Tecnico nao encontrado"));

        mockMvc.perform(get("/tecnicos/99"))
                .andExpect(status().isNotFound());
    }
}