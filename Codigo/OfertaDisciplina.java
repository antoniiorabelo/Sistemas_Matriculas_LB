package Codigo;

import java.util.ArrayList;
import java.util.List;

public class OfertaDisciplina {
    private String id;
    private int capacidadeMaxima = Validacao.MAXIMO_ALUNOS;
    private StatusOferta status;
    private Disciplina disciplina;
    private Professor professor;
    private CurriculoSemestral curriculo;
    private final List<Matricula> matriculas = new ArrayList<>();

    public OfertaDisciplina(
            String id,
            Disciplina disciplina,
            Professor professor,
            CurriculoSemestral curriculo) {
        this.id = id;
        this.disciplina = disciplina;
        this.professor = professor;
        this.curriculo = curriculo;
        this.status = StatusOferta.PLANEJADA;
    }

    public int consultarQuantidadeMatriculados() {
        return (int) matriculas.stream().filter(Matricula::estaAtiva).count();
    }

    public int consultarVagasDisponiveis() {
        return capacidadeMaxima - consultarQuantidadeMatriculados();
    }

    public boolean possuiVaga() {
        return consultarVagasDisponiveis() > 0;
    }

    public StatusOferta avaliarOferta() {
        if (consultarQuantidadeMatriculados() < Validacao.MINIMO_ALUNOS) {
            cancelarOferta();
        } else {
            confirmarOferta();
        }
        return status;
    }

    public void encerrarInscricoes() {
        status = StatusOferta.SEM_VAGAS;
    }

    public void cancelarOferta() {
        status = StatusOferta.CANCELADA;
    }

    public void confirmarOferta() {
        status = StatusOferta.CONFIRMADA;
    }

    public void abrirInscricoes() {
        status = StatusOferta.INSCRICOES_ABERTAS;
    }

    public void adicionarMatricula(Matricula matricula) {
        if (matricula != null && possuiVaga()) {
            matriculas.add(matricula);
            if (!possuiVaga()) {
                encerrarInscricoes();
            }
        }
    }

    public List<Aluno> consultarAlunosMatriculados() {
        List<Aluno> alunos = new ArrayList<>();
        for (Matricula matricula : matriculas) {
            if (matricula.estaAtiva()) {
                alunos.add(matricula.getAluno());
            }
        }
        return alunos;
    }

    public String getId() {
        return id;
    }

    public StatusOferta getStatus() {
        return status;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public Professor getProfessor() {
        return professor;
    }

    public CurriculoSemestral getCurriculo() {
        return curriculo;
    }

    public List<Matricula> getMatriculas() {
        return new ArrayList<>(matriculas);
    }
}
