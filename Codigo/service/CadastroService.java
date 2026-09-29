package Codigo.service;

import Codigo.enums.TipoDisciplina;
import Codigo.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementa a HU02 - Manter dados academicos: cadastrar, consultar,
 * alterar e inativar cursos, disciplinas, professores e alunos.
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
        if (codigo == null || codigo.isBlank() || nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Codigo e nome do curso sao obrigatorios.");
        }
        if (buscarCurso(codigo).isPresent()) {
            throw new IllegalArgumentException("Ja existe um curso com o codigo " + codigo + ".");
        }
        Curso curso = new Curso(codigo, nome, numeroCreditos);
        cursos.add(curso);
        return curso;
    }

    public Optional<Curso> buscarCurso(String codigo) {
        return cursos.stream().filter(c -> c.getCodigo().equalsIgnoreCase(codigo)).findFirst();
    }

    public List<Curso> listarCursos() {
        return new ArrayList<>(cursos);
    }

    public void associarDisciplinaAoCurso(Curso curso, Disciplina disciplina, TipoDisciplina tipo) {
        if (curso == null || disciplina == null || tipo == null) {
            throw new IllegalArgumentException("Curso, disciplina e tipo sao obrigatorios.");
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
        if (codigo == null || codigo.isBlank() || nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Codigo e nome da disciplina sao obrigatorios.");
        }
        if (buscarDisciplina(codigo).isPresent()) {
            throw new IllegalArgumentException("Ja existe uma disciplina com o codigo " + codigo + ".");
        }
        Disciplina disciplina = new Disciplina(codigo, nome, numeroCreditos);
        disciplinas.add(disciplina);
        return disciplina;
    }

    public Optional<Disciplina> buscarDisciplina(String codigo) {
        return disciplinas.stream().filter(d -> d.getCodigo().equalsIgnoreCase(codigo)).findFirst();
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
        validarNovoLogin(login);
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
        validarNovoLogin(login);
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

    public void inativarUsuario(Usuario usuario) {
        if (usuario != null) {
            usuario.inativar();
        }
    }

    private void validarNovoLogin(String login) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("Login e obrigatorio.");
        }
        if (autenticacaoService.existeLogin(login)) {
            throw new IllegalArgumentException("Ja existe um usuario com o login " + login + ".");
        }
    }
}
