package Codigo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CurriculoSemestral {
    private String id;
    private LocalDate dataCriacao;
    private boolean publicado;
    private Curso curso;
    private Semestre semestre;
    private final List<OfertaDisciplina> ofertas = new ArrayList<>();

    public CurriculoSemestral(String id, Curso curso, Semestre semestre) {
        this.id = id;
        this.curso = curso;
        this.semestre = semestre;
        this.dataCriacao = LocalDate.now();
    }

    public void adicionarOferta(OfertaDisciplina oferta) {
        if (oferta != null && !ofertas.contains(oferta)) {
            ofertas.add(oferta);
        }
    }

    public void removerOferta(OfertaDisciplina oferta) {
        ofertas.remove(oferta);
    }

    public void publicar() {
        publicado = true;
    }

    public String getId() {
        return id;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public boolean isPublicado() {
        return publicado;
    }

    public Curso getCurso() {
        return curso;
    }

    public Semestre getSemestre() {
        return semestre;
    }

    public List<OfertaDisciplina> getOfertas() {
        return new ArrayList<>(ofertas);
    }
}
