package Codigo.repository;

import Codigo.model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio em memoria compartilhado por todos os servicos.
 * Como o sistema roda apenas via terminal (sem persistencia em banco de dados
 * ou arquivo), os dados existem somente durante a execucao do programa.
 */
public class Repositorio {
    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Curso> cursos = new ArrayList<>();
    private final List<Disciplina> disciplinas = new ArrayList<>();
    private final List<Semestre> semestres = new ArrayList<>();
    private final List<CurriculoSemestral> curriculos = new ArrayList<>();
    private final List<NotificacaoCobranca> notificacoes = new ArrayList<>();

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public List<Curso> getCursos() {
        return cursos;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }

    public List<Semestre> getSemestres() {
        return semestres;
    }

    public List<CurriculoSemestral> getCurriculos() {
        return curriculos;
    }

    public List<NotificacaoCobranca> getNotificacoes() {
        return notificacoes;
    }

    public List<Aluno> getAlunos() {
        List<Aluno> alunos = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u instanceof Aluno) {
                alunos.add((Aluno) u);
            }
        }
        return alunos;
    }

    public List<Professor> getProfessores() {
        List<Professor> professores = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u instanceof Professor) {
                professores.add((Professor) u);
            }
        }
        return professores;
    }

    public List<Secretaria> getSecretarias() {
        List<Secretaria> secretarias = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u instanceof Secretaria) {
                secretarias.add((Secretaria) u);
            }
        }
        return secretarias;
    }
}
