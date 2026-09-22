package Codigo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Curso {
    private String codigo;
    private String nome;
    private int numeroCreditos;
    private boolean ativo;
    private final List<ComponenteCurricular> componentesCurriculares = new ArrayList<>();

    public Curso(String codigo, String nome, int numeroCreditos) {
        this.codigo = codigo;
        this.nome = nome;
        this.numeroCreditos = numeroCreditos;
        this.ativo = true;
    }

    public void adicionarDisciplina(Disciplina disciplina, TipoDisciplina tipo) {
        if (disciplina != null && tipo != null) {
            componentesCurriculares.add(
                    new ComponenteCurricular(UUID.randomUUID().toString(), disciplina, tipo));
        }
    }

    public void removerDisciplina(Disciplina disciplina) {
        componentesCurriculares.removeIf(
                componente -> componente.getDisciplina().equals(disciplina));
    }

    public void inativar() {
        ativo = false;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public int getNumeroCreditos() {
        return numeroCreditos;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public List<ComponenteCurricular> getComponentesCurriculares() {
        return new ArrayList<>(componentesCurriculares);
    }
}
