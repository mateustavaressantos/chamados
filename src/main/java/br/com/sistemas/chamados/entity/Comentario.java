package br.com.sistemas.chamados.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "comentarios")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String texto;

    @Column(nullable = false, length = 100)
    private String autor;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @ManyToOne
    @JoinColumn(name = "chamado_id", nullable = false)
    private Chamado chamado;

    public Comentario() {}

    public Comentario(String texto, String autor, Chamado chamado) {
        this.texto = texto;
        this.autor = autor;
        this.chamado = chamado;
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() { 
        return id; 
    }

    public String getTexto() { 
        return texto; 
    }

    public String getAutor() { 
        return autor; 
    }

    public LocalDateTime getDataHora() { 
        return dataHora; 
    }

    public Chamado getChamado() { 
        return chamado; 
    }
}