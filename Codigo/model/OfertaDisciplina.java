package Codigo.model;

import Codigo.Validacao;
import Codigo.enums.StatusOferta;
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
        int total = 0;
        for (Matricula matricula : matriculas) {
            if (matricula.estaAtiva()) {
                total++;
            }
        }
        return total;
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

    /** Usado pela persistencia para restaurar o status gravado em arquivo. */
    public void restaurarStatus(StatusOferta status) {
        this.status = status;
    }

    public void adicionarMatricula(Matricula matricula) {
        if (matricula != null && possuiVaga()) {
            matriculas.add(matricula);
            if (!possuiVaga()) {
                encerrarInscricoes();
            }
        }
    }

    /**
     * Usado pela persistencia: adiciona uma matricula ja existente (ativa ou
     * cancelada) sem aplicar regras de vaga nem alterar o status da oferta.
     */
    public void restaurarMatricula(Matricula matricula) {
        if (matricula != null && !matriculas.contains(matricula)) {
            matriculas.add(matricula);
        }
    }

    public void liberarVaga() {
        // Chamado apos um cancelamento dentro do periodo permitido (HU07):
        // se a oferta estava sem vagas, reabre as inscricoes.
        if (status == StatusOferta.SEM_VAGAS && possuiVaga()) {
            abrirInscricoes();
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

    @Override
    public String toString() {
        return disciplina.getNome() + " (Prof. " + professor.getNome() + ") - "
                + consultarQuantidadeMatriculados() + "/" + capacidadeMaxima + " alunos - " + status;
    }
}
