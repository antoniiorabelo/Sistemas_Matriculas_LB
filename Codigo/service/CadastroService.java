package Codigo.service;

import Codigo.enums.TipoDisciplina;
import Codigo.model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * HU02 - Manter dados academicos: cadastrar, consultar e inativar
 * cursos, disciplinas, professores e alunos.
 */
public class CadastroService {
    private final List<Curso> cursos;
    private final List<Disciplina> disciplinas;
    private final List<Usuario> usuarios;
    private final AutenticacaoService autenticacaoService;

    public CadastroService(List<Curso> cursos, List<Disciplina> disciplinas,
                           List<Usuario> usuarios, AutenticacaoService autenticacaoService) {
        this.cursos = cursos;
        this.disciplinas = disciplinas;
        this.usuarios = usuarios;
        this.autenticacaoService = autenticacaoService;
    }

    // ---------- Curso ----------
    public Curso cadastrarCurso(String codigo, String nome, int numeroCreditos) {
        if (vazio(codigo) || vazio(nome)) {
            throw new IllegalArgumentException("Codigo e nome do curso sao obrigatorios.");
        }
        if (buscarCurso(codigo) != null) {
            throw new IllegalArgumentException("Ja existe um curso com o codigo " + codigo + ".");
        }
        Curso curso = new Curso(codigo, nome, numeroCreditos);
        cursos.add(curso);
        return curso;
    }

    public Curso buscarCurso(String codigo) {
        for (Curso c : cursos) {
            if (c.getCodigo().equalsIgnoreCase(codigo)) {
                return c;
            }
        }
        return null;
    }

    public List<Curso> listarCursos() {
        return new ArrayList<>(cursos);
    }

    public void associarDisciplinaAoCurso(Curso curso, Disciplina disciplina, TipoDisciplina tipo) {
        if (curso == null || disciplina == null || tipo == null) {
            throw new IllegalArgumentException("Curso, disciplina e tipo sao obrigatorios.");
        }
        for (ComponenteCurricular c : curso.getComponentesCurriculares()) {
            if (c.getDisciplina().equals(disciplina)) {
                throw new IllegalArgumentException("Esta disciplina ja esta associada ao curso.");
            }
        }
        curso.adicionarDisciplina(disciplina, tipo);
    }

    public void inativarCurso(Curso curso) {
        if (curso != null) {
            curso.inativar();
        }
    }

    // ---------- Disciplina ----------
    public Disciplina cadastrarDisciplina(String codigo, String nome, int numeroCreditos) {
        if (vazio(codigo) || vazio(nome)) {
            throw new IllegalArgumentException("Codigo e nome da disciplina sao obrigatorios.");
        }
        if (buscarDisciplina(codigo) != null) {
            throw new IllegalArgumentException("Ja existe uma disciplina com o codigo " + codigo + ".");
        }
        Disciplina disciplina = new Disciplina(codigo, nome, numeroCreditos);
        disciplinas.add(disciplina);
        return disciplina;
    }

    public Disciplina buscarDisciplina(String codigo) {
        for (Disciplina d : disciplinas) {
            if (d.getCodigo().equalsIgnoreCase(codigo)) {
                return d;
            }
        }
        return null;
    }

    public List<Disciplina> listarDisciplinas() {
        return new ArrayList<>(disciplinas);
    }

    public void inativarDisciplina(Disciplina disciplina) {
        if (disciplina != null) {
            disciplina.inativar();
        }
    }

    // ---------- Professor ----------
    public Professor cadastrarProfessor(String id, String nome, String login, String senha, String registro) {
        validarNovoUsuario(nome, login, senha);
        Professor professor = new Professor(id, nome, login, senha, registro);
        usuarios.add(professor);
        return professor;
    }

    public List<Professor> listarProfessores() {
        List<Professor> professores = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u instanceof Professor) {
                professores.add((Professor) u);
            }
        }
        return professores;
    }

    // ---------- Aluno ----------
    public Aluno cadastrarAluno(String id, String nome, String login, String senha, String matricula, Curso curso) {
        validarNovoUsuario(nome, login, senha);
        Aluno aluno = new Aluno(id, nome, login, senha, matricula, curso);
        usuarios.add(aluno);
        return aluno;
    }

    public List<Aluno> listarAlunos() {
        List<Aluno> alunos = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u instanceof Aluno) {
                alunos.add((Aluno) u);
            }
        }
        return alunos;
    }

    public Usuario buscarUsuarioPorLogin(String login) {
        for (Usuario u : usuarios) {
            if (u.getLogin().equals(login)) {
                return u;
            }
        }
        return null;
    }

    public void inativarUsuario(Usuario usuario) {
        if (usuario != null) {
            usuario.inativar();
        }
    }

    private void validarNovoUsuario(String nome, String login, String senha) {
        if (vazio(nome) || vazio(login) || vazio(senha)) {
            throw new IllegalArgumentException("Nome, login e senha sao obrigatorios.");
        }
        if (autenticacaoService.existeLogin(login)) {
            throw new IllegalArgumentException("Ja existe um usuario com o login " + login + ".");
        }
    }

    private static boolean vazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
