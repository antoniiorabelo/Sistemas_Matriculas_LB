package Codigo.service;

import Codigo.Validacao;
import Codigo.enums.StatusOferta;
import Codigo.enums.TipoDisciplina;
import Codigo.enums.TipoOperacaoCobranca;
import Codigo.gateway.SistemaCobrancasGateway;
import Codigo.model.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MatriculaService {
    private final SistemaCobrancasGateway gateway;
    private final List<NotificacaoCobranca> notificacoes;
    private String ultimoErro;

    public MatriculaService(SistemaCobrancasGateway gateway, List<NotificacaoCobranca> notificacoes) {
        this.gateway = gateway;
        this.notificacoes = notificacoes;
    }

    public Matricula realizarMatricula(Aluno aluno, OfertaDisciplina oferta) {
        ultimoErro = null;
        if (aluno == null || oferta == null) {
            ultimoErro = "Aluno ou oferta invalidos.";
            return null;
        }
        Semestre semestre = oferta.getCurriculo().getSemestre();
        if (!validarPeriodo(semestre)) {
            ultimoErro = "O periodo de matriculas nao esta aberto.";
            return null;
        }
        if (oferta.getStatus() == StatusOferta.CANCELADA) {
            ultimoErro = "Esta oferta foi cancelada.";
            return null;
        }
        if (!validarVagas(oferta)) {
            ultimoErro = "A disciplina ja atingiu o limite de " + Validacao.MAXIMO_ALUNOS + " alunos.";
            return null;
        }
        if (!validarDuplicidade(aluno, oferta)) {
            ultimoErro = "Voce ja esta matriculado nesta disciplina.";
            return null;
        }
        ComponenteCurricular componente = localizarComponente(aluno, oferta);
        if (componente == null) {
            ultimoErro = "Esta disciplina nao faz parte do seu curso.";
            return null;
        }
        if (!validarLimites(aluno, semestre, componente.getTipo())) {
            ultimoErro = componente.getTipo() == TipoDisciplina.OBRIGATORIA
                    ? "Limite de " + Validacao.LIMITE_OBRIGATORIAS + " disciplinas obrigatorias atingido."
                    : "Limite de " + Validacao.LIMITE_OPTATIVAS + " disciplinas optativas atingido.";
            return null;
        }
        Matricula matricula = new Matricula(UUID.randomUUID().toString(), aluno, oferta);
        matricula.confirmar();
        aluno.adicionarMatricula(matricula);
        oferta.adicionarMatricula(matricula);

        // HU11 - notificar o Sistema de Cobrancas apos a confirmacao.
        notificar(aluno, semestre, TipoOperacaoCobranca.INCLUSAO, matricula);
        return matricula;
    }

    public boolean cancelarMatricula(Aluno aluno, Matricula matricula) {
        ultimoErro = null;
        if (aluno == null || matricula == null || matricula.getAluno() != aluno) {
            ultimoErro = "Voce so pode cancelar matriculas proprias.";
            return false;
        }
        if (!matricula.estaAtiva()) {
            ultimoErro = "Esta matricula ja esta cancelada.";
            return false;
        }
        Semestre semestre = matricula.getOferta().getCurriculo().getSemestre();
        if (!validarPeriodo(semestre)) {
            ultimoErro = "O periodo de matriculas nao esta aberto.";
            return false;
        }
        matricula.cancelar();
        matricula.getOferta().liberarVaga();

        // HU08/HU11 - cancelamento dentro do prazo gera atualizacao para cobrancas.
        notificar(aluno, semestre, TipoOperacaoCobranca.CANCELAMENTO, matricula);
        return true;
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
        int quantidade = 0;
        for (Matricula matricula : aluno.consultarMatriculas(semestre)) {
            if (!matricula.estaAtiva()) continue;
            ComponenteCurricular c = localizarComponente(aluno, matricula.getOferta());
            if (c != null && c.getTipo() == tipo) {
                quantidade++;
            }
        }
        return tipo == TipoDisciplina.OBRIGATORIA
                ? quantidade < Validacao.LIMITE_OBRIGATORIAS
                : quantidade < Validacao.LIMITE_OPTATIVAS;
    }

    public boolean validarDuplicidade(Aluno aluno, OfertaDisciplina oferta) {
        if (aluno == null || oferta == null || oferta.getCurriculo() == null) {
            return false;
        }
        for (Matricula matricula : aluno.consultarMatriculas(oferta.getCurriculo().getSemestre())) {
            if (matricula.estaAtiva()
                    && matricula.getOferta().getDisciplina().equals(oferta.getDisciplina())) {
                return false;
            }
        }
        return true;
    }

    public String getUltimoErro() {
        return ultimoErro;
    }

    private ComponenteCurricular localizarComponente(Aluno aluno, OfertaDisciplina oferta) {
        if (aluno == null || aluno.getCurso() == null || oferta == null) {
            return null;
        }
        for (ComponenteCurricular c : aluno.getCurso().getComponentesCurriculares()) {
            if (c.getDisciplina().equals(oferta.getDisciplina())) {
                return c;
            }
        }
        return null;
    }

    private void notificar(Aluno aluno, Semestre semestre, TipoOperacaoCobranca tipo, Matricula matricula) {
        NotificacaoCobranca notificacao = new NotificacaoCobranca(
                UUID.randomUUID().toString(), tipo, aluno, semestre);
        boolean sucesso;
        try {
            sucesso = tipo == TipoOperacaoCobranca.INCLUSAO
                    ? gateway.notificarMatricula(aluno, semestre, Collections.singletonList(matricula))
                    : gateway.notificarCancelamento(aluno, semestre, matricula);
        } catch (RuntimeException e) {
            sucesso = false;
        }
        if (sucesso) {
            notificacao.marcarComoEnviada();
        } else {
            notificacao.registrarFalha("Falha ao comunicar o Sistema de Cobrancas.");
        }
        notificacoes.add(notificacao);
    }
}
