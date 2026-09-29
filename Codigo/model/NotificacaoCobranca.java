package Codigo.model;

import Codigo.enums.StatusNotificacao;
import Codigo.enums.TipoOperacaoCobranca;
import java.time.LocalDateTime;

public class NotificacaoCobranca {
    private String id;
    private LocalDateTime dataEnvio;
    private TipoOperacaoCobranca tipoOperacao;
    private StatusNotificacao status;
    private String mensagemErro;
    private Aluno aluno;
    private Semestre semestre;

    public NotificacaoCobranca(String id, TipoOperacaoCobranca tipoOperacao, Aluno aluno, Semestre semestre) {
        this.id = id;
        this.tipoOperacao = tipoOperacao;
        this.aluno = aluno;
        this.semestre = semestre;
        this.status = StatusNotificacao.PENDENTE;
    }

    public void marcarComoEnviada() {
        status = StatusNotificacao.ENVIADA;
        dataEnvio = LocalDateTime.now();
        mensagemErro = null;
    }

    public void registrarFalha(String mensagem) {
        status = StatusNotificacao.FALHA;
        dataEnvio = LocalDateTime.now();
        mensagemErro = mensagem;
    }

    public StatusNotificacao getStatus() {
        return status;
    }

    public TipoOperacaoCobranca getTipoOperacao() {
        return tipoOperacao;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Semestre getSemestre() {
        return semestre;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    @Override
    public String toString() {
        return "Notificacao[" + tipoOperacao + " - " + aluno.getNome() + " - " + status + "]";
    }
}
