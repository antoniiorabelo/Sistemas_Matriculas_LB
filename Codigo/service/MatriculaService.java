package Codigo.service;

import Codigo.Validacao;
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

    public MatriculaService(SistemaCobrancasGateway gateway, List<NotificacaoCobranca> notificacoes) {
        this.gateway = gateway;
        this.notificacoes = notificacoes;
    }

    public Matricula realizarMatricula(Aluno aluno, OfertaDisciplina oferta) {
        if (aluno == null || oferta == null
                || !validarPeriodo(oferta.getCurriculo().getSemestre())
                || !validarVagas(oferta)
                || !validarDuplicidade(aluno, oferta)) {
            return null;
        }
        ComponenteCurricular componente = localizarComponente(aluno, oferta);
        if (componente == null || !validarLimites(aluno, oferta.getCurriculo().getSemestre(), componente.getTipo())) {
            return null;
        }
        Matricula matricula = new Matricula(UUID.randomUUID().toString(), aluno, oferta);
        matricula.confirmar();
        aluno.adicionarMatricula(matricula);
        oferta.adicionarMatricula(matricula);

        // HU11 - Notificar o Sistema de Cobrancas apos a confirmacao da matricula.
        notificar(aluno, oferta.getCurriculo().getSemestre(),
                TipoOperacaoCobranca.INCLUSAO, matricula, Collections.singletonList(matricula));
        return matricula;
    }

    public boolean cancelarMatricula(Aluno aluno, Matricula matricula) {
        if (aluno == null || matricula == null || matricula.getAluno() != aluno
                || !matricula.estaAtiva()
                || !validarPeriodo(matricula.getOferta().getCurriculo().getSemestre())) {
            return false;
        }
        matricula.cancelar();
        matricula.getOferta().liberarVaga();

        // HU08/HU11 - Cancelamentos dentro do prazo geram atualizacao para o Sistema de Cobrancas.
        notificar(aluno, matricula.getOferta().getCurriculo().getSemestre(),
                TipoOperacaoCobranca.CANCELAMENTO, matricula, Collections.singletonList(matricula));
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
        long quantidade = aluno.consultarMatriculas(semestre).stream()
                .filter(Matricula::estaAtiva)
                .filter(matricula -> {
                    ComponenteCurricular c = localizarComponente(aluno, matricula.getOferta());
                    return c != null && c.getTipo() == tipo;
                })
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

    private ComponenteCurricular localizarComponente(Aluno aluno, OfertaDisciplina oferta) {
        if (aluno == null || aluno.getCurso() == null || oferta == null) {
            return null;
        }
        return aluno.getCurso().getComponentesCurriculares().stream()
                .filter(c -> c.getDisciplina().equals(oferta.getDisciplina()))
                .findFirst()
                .orElse(null);
    }

    private void notificar(Aluno aluno, Semestre semestre, TipoOperacaoCobranca tipo,
                            Matricula matricula, List<Matricula> matriculas) {
        NotificacaoCobranca notificacao = new NotificacaoCobranca(
                UUID.randomUUID().toString(), tipo, aluno, semestre);
        boolean sucesso = tipo == TipoOperacaoCobranca.INCLUSAO
                ? gateway.notificarMatricula(aluno, semestre, matriculas)
                : gateway.notificarCancelamento(aluno, semestre, matricula);
        if (sucesso) {
            notificacao.marcarComoEnviada();
        } else {
            notificacao.registrarFalha("Falha ao comunicar o Sistema de Cobrancas.");
        }
        notificacoes.add(notificacao);
    }
}
