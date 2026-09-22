package Codigo;

import java.util.UUID;

public class MatriculaService {
    public Matricula realizarMatricula(Aluno aluno, OfertaDisciplina oferta) {
        if (aluno == null || oferta == null
                || !validarPeriodo(oferta.getCurriculo().getSemestre())
                || !validarVagas(oferta)
                || !validarDuplicidade(aluno, oferta)) {
            return null;
        }

        Matricula matricula = new Matricula(UUID.randomUUID().toString(), aluno, oferta);
        matricula.confirmar();
        aluno.adicionarMatricula(matricula);
        oferta.adicionarMatricula(matricula);
        return matricula;
    }

    public void cancelarMatricula(Aluno aluno, Matricula matricula) {
        if (aluno != null && matricula != null && matricula.getAluno() == aluno
                && matricula.estaAtiva()) {
            matricula.cancelar();
        }
    }

    public boolean validarPeriodo(Semestre semestre) {
        return semestre != null
                && semestre.getPeriodoMatricula() != null
                && semestre.getPeriodoMatricula().estaAberto();
    }

    public boolean validarVagas(OfertaDisciplina oferta) {
        return oferta != null && oferta.possuiVaga();
    }

    public boolean validarLimites(Aluno aluno, Semestre semestre, TipoDisciplina tipo) {
        if (aluno == null || semestre == null || tipo == null) {
            return false;
        }

        long quantidade = aluno.consultarMatriculas(semestre).stream()
                .filter(Matricula::estaAtiva)
                .filter(matricula -> matricula.getOferta().getCurriculo().getCurso()
                        .getComponentesCurriculares().stream()
                        .anyMatch(componente -> componente.getDisciplina()
                                .equals(matricula.getOferta().getDisciplina())
                                && componente.getTipo() == tipo))
                .count();

        return tipo == TipoDisciplina.OBRIGATORIA
                ? quantidade < Validacao.LIMITE_OBRIGATORIAS
                : quantidade < Validacao.LIMITE_OPTATIVAS;
    }

    public boolean validarDuplicidade(Aluno aluno, OfertaDisciplina oferta) {
        if (aluno == null || oferta == null || oferta.getCurriculo() == null) {
            return false;
        }
        return aluno.consultarMatriculas(oferta.getCurriculo().getSemestre()).stream()
                .noneMatch(matricula -> matricula.estaAtiva()
                        && matricula.getOferta() == oferta);
    }
}
