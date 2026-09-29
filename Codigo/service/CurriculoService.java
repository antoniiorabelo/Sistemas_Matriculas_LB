package Codigo.service;

import Codigo.enums.StatusOferta;
import Codigo.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CurriculoService {
    private final List<CurriculoSemestral> curriculos;

    public CurriculoService() {
        this.curriculos = new ArrayList<>();
    }

    /** Compartilha a mesma lista de curriculos do repositorio central. */
    public CurriculoService(List<CurriculoSemestral> curriculos) {
        this.curriculos = curriculos;
    }

    public CurriculoSemestral criarCurriculo(Curso curso, Semestre semestre) {
        CurriculoSemestral existente = buscarCurriculo(curso, semestre);
        if (existente != null) {
            return existente;
        }
        CurriculoSemestral curriculo = new CurriculoSemestral(UUID.randomUUID().toString(), curso, semestre);
        curriculos.add(curriculo);
        return curriculo;
    }

    public CurriculoSemestral buscarCurriculo(Curso curso, Semestre semestre) {
        for (CurriculoSemestral c : curriculos) {
            if (c.getCurso() == curso && c.getSemestre() == semestre) {
                return c;
            }
        }
        return null;
    }

    public List<CurriculoSemestral> listarCurriculosDoSemestre(Semestre semestre) {
        List<CurriculoSemestral> resultado = new ArrayList<>();
        for (CurriculoSemestral c : curriculos) {
            if (c.getSemestre() == semestre) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    /** HU03 - somente disciplinas ativas podem integrar o curriculo. */
    public OfertaDisciplina adicionarOferta(
            CurriculoSemestral curriculo,
            Disciplina disciplina,
            Professor professor) {
        if (!disciplina.isAtiva()) {
            throw new IllegalArgumentException("Somente disciplinas ativas podem integrar o curriculo.");
        }
        OfertaDisciplina oferta = new OfertaDisciplina(
                UUID.randomUUID().toString(), disciplina, professor, curriculo);
        oferta.abrirInscricoes();
        curriculo.adicionarOferta(oferta);
        professor.adicionarOferta(oferta);
        return oferta;
    }

    /** HU05 - ofertas do curriculo do curso do aluno no semestre informado. */
    public List<OfertaDisciplina> consultarOfertasDoAluno(Aluno aluno, Semestre semestre) {
        if (aluno == null || aluno.getCurso() == null || semestre == null) {
            return new ArrayList<>();
        }
        CurriculoSemestral curriculo = buscarCurriculo(aluno.getCurso(), semestre);
        return curriculo == null ? new ArrayList<OfertaDisciplina>() : curriculo.getOfertas();
    }

    public void encerrarPeriodo(Semestre semestre) {
        if (semestre != null && semestre.getPeriodoMatricula() != null) {
            semestre.getPeriodoMatricula().encerrar();
        }
    }

    /** HU09 - confirma ofertas com 3 a 60 alunos ativos e cancela as com menos de 3. */
    public void avaliarOfertas(Semestre semestre) {
        for (CurriculoSemestral curriculo : listarCurriculosDoSemestre(semestre)) {
            for (OfertaDisciplina oferta : curriculo.getOfertas()) {
                oferta.avaliarOferta();
            }
        }
    }

    public List<Aluno> identificarAlunosAfetados(OfertaDisciplina oferta) {
        if (oferta != null && oferta.getStatus() == StatusOferta.CANCELADA) {
            return oferta.consultarAlunosMatriculados();
        }
        return new ArrayList<>();
    }

    public List<OfertaDisciplina> listarOfertasCanceladas(Semestre semestre) {
        List<OfertaDisciplina> canceladas = new ArrayList<>();
        for (CurriculoSemestral curriculo : listarCurriculosDoSemestre(semestre)) {
            for (OfertaDisciplina oferta : curriculo.getOfertas()) {
                if (oferta.getStatus() == StatusOferta.CANCELADA) {
                    canceladas.add(oferta);
                }
            }
        }
        return canceladas;
    }
}
