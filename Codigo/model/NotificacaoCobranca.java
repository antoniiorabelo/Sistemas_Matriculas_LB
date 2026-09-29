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

    /** Usado pela persistencia para restaurar uma notificacao gravada em arquivo. */
    public NotificacaoCobranca(String id, TipoOperacaoCobranca tipoOperacao, Aluno aluno, Semestre semestre,
                               StatusNotificacao status, LocalDateTime dataEnvio, String mensagemErro) {
        this(id, tipoOperacao, aluno, semestre);
        this.status = status;
        this.dataEnvio = dataEnvio;
        this.mensagemErro = mensagemErro;
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

    public String getId() {
        return id;
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

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    @Override
    public String toString() {
        return "Notificacao[" + tipoOperacao + " - " + aluno.getNome() + " - " + status + "]";
    }
}
