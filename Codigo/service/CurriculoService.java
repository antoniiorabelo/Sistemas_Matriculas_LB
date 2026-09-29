package Codigo.service;

import Codigo.enums.StatusOferta;
import Codigo.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CurriculoService {
    private final List<CurriculoSemestral> curriculos;

    public CurriculoService() {
        this.curriculos = new ArrayList<>();
    }

    /**
     * Permite compartilhar a mesma lista de curriculos do repositorio central,
     * necessaria para localizar a oferta correta de um aluno/professor
     * e para avaliar todas as ofertas ao encerrar um periodo (HU09).
     */
    public CurriculoService(List<CurriculoSemestral> curriculos) {
        this.curriculos = curriculos;
    }

    public CurriculoSemestral criarCurriculo(Curso curso, Semestre semestre) {
        Optional<CurriculoSemestral> existente = buscarCurriculo(curso, semestre);
        if (existente.isPresent()) {
            return existente.get();
        }
        CurriculoSemestral curriculo = new CurriculoSemestral(UUID.randomUUID().toString(), curso, semestre);
        curriculos.add(curriculo);
        return curriculo;
    }

    public Optional<CurriculoSemestral> buscarCurriculo(Curso curso, Semestre semestre) {
        return curriculos.stream()
                .filter(c -> c.getCurso() == curso && c.getSemestre() == semestre)
                .findFirst();
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

    public OfertaDisciplina adicionarOferta(
            CurriculoSemestral curriculo,
            Disciplina disciplina,
            Professor professor) {
        OfertaDisciplina oferta = new OfertaDisciplina(
                UUID.randomUUID().toString(), disciplina, professor, curriculo);
        oferta.abrirInscricoes();
        curriculo.adicionarOferta(oferta);
        professor.adicionarOferta(oferta);
        return oferta;
    }

    /**
     * HU05 - Consultar disciplinas ofertadas: retorna as ofertas do curriculo
     * correspondente ao curso do aluno no semestre informado.
     */
    public List<OfertaDisciplina> consultarOfertasDoAluno(Aluno aluno, Semestre semestre) {
        if (aluno == null || aluno.getCurso() == null || semestre == null) {
            return new ArrayList<>();
        }
        return buscarCurriculo(aluno.getCurso(), semestre)
                .map(CurriculoSemestral::getOfertas)
                .orElse(new ArrayList<>());
    }

    public void encerrarPeriodo(Semestre semestre) {
        if (semestre != null && semestre.getPeriodoMatricula() != null) {
            semestre.getPeriodoMatricula().encerrar();
        }
    }

    /**
     * HU09 - Avaliar a oferta ao encerrar o periodo: para cada curriculo do
     * semestre, avalia todas as ofertas (confirma as com 3 a 60 alunos ativos
     * e cancela as com menos de 3).
     */
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
