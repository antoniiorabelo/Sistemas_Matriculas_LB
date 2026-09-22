package Codigo;

import java.time.LocalDateTime;

public class NotificacaoCobranca {
    private String id;
    private LocalDateTime dataEnvio;
    private TipoOperacaoCobranca tipoOperacao;
    private StatusNotificacao status;
    private String mensagemErro;

    public NotificacaoCobranca(String id, TipoOperacaoCobranca tipoOperacao) {
        this.id = id;
        this.tipoOperacao = tipoOperacao;
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
}
