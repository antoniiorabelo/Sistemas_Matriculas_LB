package Codigo.model;

import java.util.ArrayList;
import java.util.List;

public class Aluno extends Usuario {
    private String matricula;
    private Curso curso;
    private final List<Matricula> matriculas = new ArrayList<>();

    public Aluno(String id, String nome, String login, String senha, String matricula) {
        super(id, nome, login, senha);
        this.matricula = matricula;
    }

    public Aluno(String id, String nome, String login, String senha, String matricula, Curso curso) {
        this(id, nome, login, senha, matricula);
        this.curso = curso;
    }

    public List<OfertaDisciplina> consultarOfertas(Semestre semestre) {
        // A consulta real e feita via CurriculoService, que tem acesso
        // ao repositorio de curriculos semestrais (ver Codigo.service.CurriculoService).
        return new ArrayList<>();
    }

    public List<Matricula> consultarMatriculas(Semestre semestre) {
        List<Matricula> resultado = new ArrayList<>();
        for (Matricula matriculaRealizada : matriculas) {
            if (matriculaRealizada.pertenceAoSemestre(semestre)) {
                resultado.add(matriculaRealizada);
            }
        }
        return resultado;
    }

    public void adicionarMatricula(Matricula matriculaRealizada) {
        matriculas.add(matriculaRealizada);
    }

    public String getMatricula() {
        return matricula;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }
}
